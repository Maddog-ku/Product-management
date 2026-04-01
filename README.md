# Product management

一個以 Android 原生 Java 開發的本地端庫存管理應用，提供商品管理、分類管理、盤點、簡易銷售扣庫存，以及到期提醒功能。專案目前採用 Room 作為本機資料庫，適合用於小型商品盤存、門市內部管理，或作為 Android CRUD 專案練習。

## 專案特色

- 商品新增、編輯、刪除
- 商品分類管理
- 商品名稱搜尋與分類篩選
- 商品照片支援相機拍照與相簿選取
- 庫存盤點與批次更新
- 簡易銷售流程與結帳後自動扣庫存
- 商品到期日與提前通知天數設定
- 使用 WorkManager 進行每日到期檢查與單次提醒排程

## 技術棧

- 語言: Java 17
- 平台: Android
- 最低支援版本: Android 7.0 `minSdk 24`
- 目標版本: Android 14 `targetSdk 34`
- UI: Material Components、RecyclerView、ViewBinding
- 資料層: Room
- 圖片載入: Glide
- 背景工作: WorkManager
- 架構方向: Activity + Repository + DAO

## 功能說明

### 1. 首頁

首頁提供三個主要入口：

- 商品管理
- 分類管理
- 盤點功能

App 啟動時也會註冊每日一次的到期檢查背景工作。

### 2. 商品管理

商品管理頁面支援：

- 顯示商品列表
- 依商品名稱搜尋
- 依分類篩選
- 點擊商品進入編輯
- 透過浮動按鈕新增商品
- 進入銷售頁面進行結帳扣庫存

商品資料目前包含：

- 商品名稱
- 分類
- 進貨價
- 售價
- 庫存
- 備註
- 商品圖片
- 到期日
- 提前通知天數
- 建立時間 / 更新時間

註:
目前程式中 `code` 欄位仍存在於資料表與資料模型，但新增/編輯畫面已不再要求輸入商品編號。

### 3. 分類管理

可在分類頁中：

- 新增分類
- 修改分類名稱
- 刪除分類

在商品管理頁側邊欄長按分類，也可以直接刪除分類。若刪除的分類已被商品使用，資料庫會將商品的 `categoryId` 設為 `null`，不會連帶刪除商品。

### 4. 盤點功能

盤點頁會列出目前商品與庫存，允許一次調整多筆商品數量，再統一送出更新。

### 5. 銷售功能

銷售頁提供簡易購物車流程：

- 調整各商品購買數量
- 即時計算總金額
- 結帳後扣除對應庫存

此流程適合做為基礎 POS / 出庫邏輯示範，目前未串接訂單紀錄、金流或發票功能。

### 6. 商品圖片

新增或編輯商品時可：

- 使用相機拍照
- 從相簿選取圖片

圖片會複製或儲存在 App 私有目錄 `files/product_images` 中，避免外部圖片路徑失效。

### 7. 到期提醒

當商品設定：

- 到期日
- 提前通知天數

系統會建立單次提醒工作，並在 App 啟動時透過每日背景檢查補強提醒。提醒透過系統通知顯示。

## 專案結構

```text
Product management/
├── app/
│   ├── src/main/java/com/example/inventoryapp/
│   │   ├── data/          # Room Entity、DAO、Database
│   │   ├── repository/    # Repository 封裝資料操作
│   │   ├── ui/            # 各 Activity 與 RecyclerView Adapter
│   │   └── worker/        # 到期通知背景工作
│   └── src/main/res/      # 版面、字串、顏色、drawable 等資源
├── build.gradle
└── settings.gradle
```

## 資料庫設計

### `categories`

- `id`
- `name`

### `products`

- `id`
- `name`
- `code`
- `imagePath`
- `categoryId`
- `costPrice`
- `salePrice`
- `stock`
- `note`
- `expiryDate`
- `notifyDays`
- `createdAt`
- `updatedAt`

目前資料庫名稱為 `inventory_app.db`，版本為 `2`。

## 權限需求

`AndroidManifest.xml` 內已宣告以下權限：

- `CAMERA`
- `READ_MEDIA_IMAGES`
- `READ_EXTERNAL_STORAGE` `maxSdkVersion=32`
- `POST_NOTIFICATIONS`

說明：

- Android 13 以上若要正常顯示通知，仍需要在執行階段向使用者請求通知權限。
- 目前專案已宣告 `POST_NOTIFICATIONS`，但尚未看到明確的執行階段請求流程，實機上可能導致提醒通知無法正常顯示。

## 開發環境

建議使用：

- Android Studio Koala 或更新版本
- Android SDK 34
- JDK 17

## 如何執行

### 方式一：使用 Android Studio

1. 用 Android Studio 開啟 `Product management` 資料夾
2. 等待 Gradle Sync 完成
3. 連接模擬器或 Android 裝置
4. 執行 `app` module

### 方式二：命令列

此專案目前目錄內沒有看到 `gradlew` / `gradlew.bat`，因此若要走命令列建置，需先補上 Gradle Wrapper，或直接由 Android Studio 產生。

## 目前限制與注意事項

- 專案以本機資料庫為主，未提供雲端同步、登入或多人協作功能。
- 銷售流程會直接扣庫存，但尚未保留完整交易紀錄。
- 通知內容目前為英文文案，與 App 主要中文介面風格尚未完全一致。
- `AppDatabase` 同時使用 `addMigrations(MIGRATION_1_2)` 與 `fallbackToDestructiveMigration()`；未來若版本再升級，仍需謹慎處理 migration 策略。
- 根目錄目前缺少 Gradle Wrapper 檔案，對命令列建置與 CI 會有影響。

