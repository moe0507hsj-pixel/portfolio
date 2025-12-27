# Backend Web API（C# / ASP.NET Core）

## 📌 プロジェクト概要
C# と ASP.NET Core を使って構築した学習用 Web API。
REST API の基本（CRUD、DI、Repository パターン）を実装し、実務相当の構成を想定。

## 🛠 技術スタック
- C# / .NET 8
- ASP.NET Core Web API
- Entity Framework Core
- SQL Server
- xUnit（任意：単体テスト用）

## 🚀 機能一覧
- ユーザー CRUD
- ログ・例外ハンドリング
- DB 接続（EF Core）
- DTO / モデル分離

## 📁 ディレクトリ構成

src/
├── Controllers/
├── Services/
├── Repositories/
├── Models/
└── Program.cs
tests/
docs/
└── architecture.md

## ▶️ 実行方法

dotnet restore
dotnet run


## 🧱 設計方針（Architecture）
- Controller / Service / Repository の3層構造
- DI コンテナを使用
- エラーハンドリングをミドルウェア化

## 📌 今後の改善
- バリデーション強化
- JWT 認証追加
