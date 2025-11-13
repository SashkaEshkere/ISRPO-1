<?php
// public/index.php
require_once __DIR__ . '/../src/Database.php';
header('Content-Type: application/json; charset=utf-8');
// настройка: путь к файлу БД
$dbFile = __DIR__ . '/../data/library.db';
$db = new Database($dbFile)->getPdo();

// — CORS для удобства (если нужно)
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Accept");

// OPTIONS preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

// простой парсер пути: ожидаем /api/...
$uri = $_SERVER['REQUEST_URI'];
$scriptName = dirname($_SERVER['SCRIPT_NAME']);
$path = substr($uri, strlen($scriptName));
$path = strtok($path, '?'); // урезаем query
$path = trim($path, "/");

// разбиваем на сегменты
$segments = explode('/', $path);

// ожидаем: api/{resource}[...]
if (count($segments) < 2 || $segments[0] !== 'api') {
    http_response_code(404);
    echo json_encode(["error" => "Not found"]);
    exit;
}

$resource = $segments[1];
$method = $_SERVER['REQUEST_METHOD'];

// читаем тело JSON если есть
$rawBody = file_get_contents('php://input');
$body = json_decode($rawBody, true);

// --- ROUTES ---
// Books:
// GET  /api/books               -> list books (id,title,author)
// GET  /api/books/withvisitors  -> books with visitors
// POST /api/books               -> create book {title,author}
// DELETE /api/books/{id}        -> delete book

// Visitors:
// GET  /api/visitors
// POST /api/visitors            -> create visitor {name}
// DELETE /api/visitors/{id}

// BookVisitors (link):
// POST /api/bookvisitors        -> add link {bookId, visitorId}
// POST /api/bookvisitors/delete -> remove link {bookId, visitorId}

// Helper functions
function jsonResponse($data, $status = 200) {
    header('Content-Type: application/json; charset=utf-8');
    http_response_code($status);
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    if ($resource === 'books') {
        // GET /api/books or /api/books/withvisitors
        if ($method === 'GET' && isset($segments[2]) && $segments[2] === 'withvisitors') {
            $stmt = $db->query("
                SELECT b.Id as BookId, b.Title, b.Author,
                       bv.VisitorId, v.Name as VisitorName
                FROM Books b
                LEFT JOIN BookVisitors bv ON b.Id = bv.BookId
                LEFT JOIN Visitors v ON bv.VisitorId = v.Id
                ORDER BY b.Id
            ");
            $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $result = [];
            foreach ($rows as $r) {
                $bid = $r['BookId'];
                if (!isset($result[$bid])) {
                    $result[$bid] = [
                        'id' => (int)$bid,
                        'title' => $r['Title'],
                        'author' => $r['Author'],
                        'visitors' => []
                    ];
                }
                if ($r['VisitorId'] !== null) {
                    $result[$bid]['visitors'][] = [
                        'id' => (int)$r['VisitorId'],
                        'name' => $r['VisitorName']
                    ];
                }
            }
            jsonResponse(array_values($result));
        }

        if ($method === 'GET') {
            $stmt = $db->query("SELECT Id, Title, Author FROM Books");
            $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
            jsonResponse($rows);
        }

        if ($method === 'POST') {
            if (!$body || empty($body['title'])) {
                jsonResponse(['error' => 'title is required'], 400);
            }
            $title = $body['title'];
            $author = isset($body['author']) ? $body['author'] : null;
            $stmt = $db->prepare("INSERT INTO Books (Title, Author) VALUES (:title, :author)");
            $stmt->execute([':title' => $title, ':author' => $author]);
            $id = (int)$db->lastInsertId();
            jsonResponse(['id' => $id, 'title' => $title, 'author' => $author], 201);
        }

        if ($method === 'DELETE' && isset($segments[2]) && is_numeric($segments[2])) {
            $id = (int)$segments[2];
            // удаляем книгу (ON DELETE CASCADE удалит связки)
            $stmt = $db->prepare("DELETE FROM Books WHERE Id = :id");
            $stmt->execute([':id' => $id]);
            jsonResponse(['deleted' => $id], 204);
        }
    }

    if ($resource === 'visitors') {
         if ($method === 'GET') {
        $stmt = $db->query("SELECT Id, Name FROM Visitors");
        $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Преобразуем ключи к нижнему регистру и корректным именам
        $out = [];
        foreach ($rows as $r) {
            $out[] = [
                'id' => isset($r['Id']) ? (int)$r['Id'] : null,
                'name' => isset($r['Name']) ? $r['Name'] : null
            ];
        }
        jsonResponse($out);
    }

        if ($method === 'POST') {
            if (!$body || empty($body['name'])) {
                jsonResponse(['error' => 'name is required'], 400);
            }
            $name = $body['name'];
            $stmt = $db->prepare("INSERT INTO Visitors (Name) VALUES (:name)");
            $stmt->execute([':name' => $name]);
            $id = (int)$db->lastInsertId();
            jsonResponse(['id' => $id, 'name' => $name], 201);
        }

        if ($method === 'DELETE' && isset($segments[2]) && is_numeric($segments[2])) {
            $id = (int)$segments[2];
            $stmt = $db->prepare("DELETE FROM Visitors WHERE Id = :id");
            $stmt->execute([':id' => $id]);
            jsonResponse(['deleted' => $id], 204);
        }
    }

    if ($resource === 'bookvisitors') {
        if ($method === 'POST' && isset($segments[2]) && $segments[2] === 'delete') {
            // удалить связь (POST /api/bookvisitors/delete) body: {bookId, visitorId}
            if (!$body || !isset($body['bookId']) || !isset($body['visitorId'])) {
                jsonResponse(['error' => 'bookId & visitorId required'], 400);
            }
            $stmt = $db->prepare("DELETE FROM BookVisitors WHERE BookId = :b AND VisitorId = :v");
            $stmt->execute([':b' => (int)$body['bookId'], ':v' => (int)$body['visitorId']]);
            jsonResponse(['deleted' => true]);
        }

        if ($method === 'POST') {
            // add link {bookId, visitorId}
            if (!$body || !isset($body['bookId']) || !isset($body['visitorId'])) {
                jsonResponse(['error' => 'bookId & visitorId required'], 400);
            }
            $stmt = $db->prepare("INSERT OR IGNORE INTO BookVisitors (BookId, VisitorId) VALUES (:b, :v)");
            $stmt->execute([':b' => (int)$body['bookId'], ':v' => (int)$body['visitorId']]);
            jsonResponse(['added' => true]);
        }
    }

    // если сюда попали — не найден маршрут
    http_response_code(404);
    echo json_encode(['error' => 'not found']);
    exit;

} catch (Exception $ex) {
    http_response_code(500);
    echo json_encode(['error' => $ex->getMessage()]);
    exit;
}
