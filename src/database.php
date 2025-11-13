<?php
// src/Database.php
class Database {
    private $pdo;
    public function __construct($dbPath) {
        if (!file_exists($dbPath)) {
            // Если нужно — можно инициализировать SQL скриптом
        }
        $this->pdo = new PDO("sqlite:" . $dbPath);
        $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        // включаем foreign keys
        $this->pdo->exec('PRAGMA foreign_keys = ON;');
    }

    public function getPdo() {
        return $this->pdo;
    }
}