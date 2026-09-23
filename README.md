<div align="center">

# Limit Breaker

**Shatter the 30-million-block virtual boundary and unlock the 32-bit mathematical limit ($X/Z = \pm 2,147,483,647$) for Minecraft 26.3.**

**打破 Minecraft 26.3 版本中 3000 萬方塊的虛擬邊界，並突破 32 位元數學極限（$X/Z = \pm 2,147,483,647$）。**

[![Minecraft](https://img.shields.io/badge/Minecraft-26.3-brightgreen.svg)](https://minecraft.net/)
[![Modloader](https://img.shields.io/badge/Modloader-Fabric-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-LGPL3.0-yellow.svg)](LICENSE)

<p>
  <b>Language / 語言：</b>
  <a href="#-english">English</a> | 
  <a href="#-繁體中文">繁體中文</a>  |
  <a href="#-简体中文">简体中文</a>
</p>

</div>

---

<details open>
<summary><b>📖 English (Click to collapse / 點擊折疊)</b></summary>

<br>

### 📖 About

In vanilla Minecraft, the playable world is confined within a strict "30-million-block" border. Crossing this barrier results in ghost blocks, entity clipping, rendering black holes, and fatal server crashes.

**Limit Breaker** is a low-level coordinate expansion and engine stabilization mod engineered specifically for **Minecraft 26.3 (Fabric)**. By re-architecting coordinate packing, hash mapping, and spatial storage dispatching, this mod completely dissolves the world border constraints, extending your world radius to the theoretical limit of a 32-bit signed integer: **2.14 billion blocks** (with a total world diameter exceeding **4.29 billion blocks**).

Even 2 billion blocks away from the origin, block interactions, structure generation, item pickups, and melee combat remain rock-solid and responsive.

### ✨ Features

#### 🌍 Full 32-Bit World Bounds
* Expands the maximum world border diameter to `(Integer.MAX_VALUE - 16) * 2` (~4.29 billion blocks).
* Seamlessly supports `/worldborder set` and dedicated server properties configuration for extreme bounds.

#### 🔢 High-Precision 64-Bit Coordinate Storage
* Completely overhauls `BlockPos`, `SectionPos`, and `ChunkPos` bit-packing to prevent coordinate bit-truncation beyond 30 million blocks.
* Utilizes memory-mapped striped hash caches to eliminate key collisions at astronomical coordinates.

#### 🖥️ Modern Rendering Pipeline Integration (Minecraft 26.3)
* **RotatingSectionStorage Support**: Native adaptation for 26.3's modern section storage, featuring horizontal toroidal modulo wrapping and dynamic vertical sliding windows.
* **Octree Stability**: Patches root bounding box power-of-two alignment, eliminating `StackOverflowError` and chunk rendering voids during frustum culling.

#### 🛡️ Hardened Server Defenses
* **World Generation**: Fixes Aquifer density volume integer overflows, mineshaft generation loops, and out-of-bounds chunk scheduling assertions.
* **Entity & Pathfinding**: Fixes `NegativeArraySizeException` during AI wandering near world edges and patches entity section lookups for accurate item collection and hit detection.
* **Lighting Engine**: Implements null-safe fallbacks for light propagation across unloaded borders, preventing `NullPointerException` (NPE).
* **Concurrency Protection**: Bypasses parallel feature generation thread-lock assertions, ensuring broad compatibility with third-party worldgen mods.

### 🚀 Quick Start

Once installed, configure your world border using administrative commands:

```bash
# 1. Expand the world border to its maximum physical diameter (~4.29 billion blocks)
/worldborder set 4294967262

# 2. Teleport to the 32-bit extreme edge
/tp @s 2147483600 100 0
```
---
### ✅Mod Status

* 32-bit coordinate unrolling (stable up to \pm 2.14 \times 10^9)
* Minecraft 26.3 RotatingSectionStorage & Octree rendering fixes
* Server-side world generation, lighting, and entity AI crash prevention
* Collision box indexing & combat/pickup recovery
---
### 🔜Roadmap
* Classic Beta 1.7.3-style Far Lands noise generation (cheese slices/stacked wall terrain currently in tuning)
</details>

<details open>
<summary><b>📖 繁體中文 (Click to collapse / 點擊折疊)</b></summary>

<br>

### 📖 簡介

在原版 Minecraft 中，可遊玩的世界被嚴格限制在「3000萬方塊」的邊界內。一旦越過此界限，便會導致幽靈方塊、實體穿模、渲染黑洞以及致命的伺服器崩潰。

**Limit Breaker** 是一款專為 **Minecraft 26.3 (Fabric)** 設計的底層座標擴展與引擎穩定性模組。透過重構座標打包、雜湊映射及空間儲存調度機制，該模組徹底消除了世界邊界限制，將世界半徑擴展至 32 位元有符號整數的理論極限：**21.4 億方塊**（世界總直徑超過 **42.9 億方塊**）。

即使在距離原點 20 億方塊之遙，方塊互動、結構生成、物品拾取及近戰格鬥依然保持極高的穩定性和反應速度。

### ✨ 特性

#### 🌍 完整的 32 位元世界邊界
* 將最大世界邊界直徑擴展至 `(Integer.MAX_VALUE - 16) * 2`（約 42.9 億方塊）。
* 無縫支援 `/worldborder set` 指令及專用伺服器（Dedicated Server）配置，以設定極值邊界。

#### 🔢 高精度 64 位元座標存儲
* 徹底重構 `BlockPos`、`SectionPos` 和 `ChunkPos` 的位元打包（bit-packing）方式，防止座標在超過 3,000 萬方塊時發生位元截斷。
* 採用記憶體映射的條帶化雜湊快取（striped hash caches），消除在超遠座標處的鍵衝突。

#### 🖥️ 現代渲染管線整合 (Minecraft 26.3)
* **RotatingSectionStorage 支援**：原生適配 26.3 的現代化區塊分段（section）儲存機制，支援水平環面模運算迴繞（toroidal modulo wrapping）及動態垂直滑動視窗。
* **八叉樹穩定性**：修復根邊界框（root bounding box）的 2 的冪次對齊問題，消除視錐剔除（frustum culling）期間的 `StackOverflowError` 及區塊渲染空洞。

#### 🛡️ 強化伺服器防禦
* **世界生成**：修復含水層（Aquifer）密度體素的整數溢出、廢棄礦井生成死循環以及越界區塊調度斷言錯誤。
* **實體與尋路**：修復了 AI 在世界邊界附近遊蕩時可能引發的 `NegativeArraySizeException`（數組大小為負異常），並優化了實體區塊查找邏輯，以確保物品拾取和碰撞檢測的準確性。
* **光照引擎**：針對跨越未載入區塊邊界的光照傳播實現了空安全（null-safe）回退機制，從而避免 `NullPointerException`（NPE，空指針異常）。
* **並發保護**：繞過了並行特徵生成（parallel feature generation）過程中的線程鎖斷言檢查，確保與第三方世界生成模組（mods）具有廣泛的兼容性。

### 🚀 快速入門

安裝完成後，請使用管理員指令配置世界邊界：

```bash
# 1. 將世界邊界擴展至最大物理直徑（約 42.9 億格）
/worldborder set 4294967262

# 2. 傳送至 32 位元座標系的極限邊緣
/tp @s 2147483600 100 0
```
---
### ✅ 模組狀態

* 32 位元座標展開支援（在 ±2.14 × 10^9 範圍內穩定運作）
* 修正了 Minecraft 26.3 版本的 `RotatingSectionStorage` 及八叉樹（Octree）渲染問題
* 防止伺服器端世界生成、光照計算及實體 AI 相關的崩潰
* 碰撞箱索引優化及戰鬥/物品拾取邏輯修復
---
### 🔜 開發路線圖
* 還原經典 Beta 1.7.3 風格的「邊境之地」（Far Lands）噪音生成效果（目前正在微調「起司」狀地形及堆疊牆體地形的生成參數）
</details>

<details open>
<summary><b>📖 繁体中文 (Click to collapse / 点击折叠)</b></summary>

<br>

### 📖 简介

在原版 Minecraft 中，可游玩的世界被严格限制在「3000万方块」的边界内。一旦越过此界限，便会导致幽灵方块、实体穿模、渲染黑洞以及致命的伺服器崩溃。

**Limit Breaker** 是一款专为 **Minecraft 26.3 (Fabric)** 设计的底层座标扩展与引擎稳定性模组。透过重构座标打包、杂凑映射及空间储存调度机制，该模组彻底消除了世界边界限制，将世界半径扩展至 32 位元有符号整数的理论极限：**21.4 亿方块**（世界总直径超过 **42.9 亿方块**）。

即使在距离原点 20 亿方块之遥，方块互动、结构生成、物品拾取及近战格斗依然保持极高的稳定性和反应速度。

### ✨ 特性

#### 🌍 完整的 32 位元世界边界
* 将最大世界边界直径扩展至 `(Integer.MAX_VALUE - 16) * 2`（约 42.9 亿方块）。
* 无缝支援 `/worldborder set` 指令及专用伺服器（Dedicated Server）配置，以设定极值边界。

#### 🔢 高精度 64 位元座标存储
* 彻底重构 `BlockPos`、`SectionPos` 和 `ChunkPos` 的位元打包（bit-packing）方式，防止座标在超过 3,000 万方块时发生位元截断。
* 采用记忆体映射的条带化杂凑快取（striped hash caches），消除在超远座标处的键冲突。

#### 🖥️ 现代渲染管线整合 (Minecraft 26.3)
* **RotatingSectionStorage 支援**：原生适配 26.3 的现代化区块分段（section）储存机制，支援水平环面模运算回绕（toroidal modulo wrapping）及动态垂直滑动视窗。
* **八叉树稳定性**：修复根边界框（root bounding box）的 2 的幂次对齐问题，消除视锥剔除（frustum culling）期间的 `StackOverflowError` 及区块渲染空洞。

#### 🛡️ 强化伺服器防御
* **世界生成**：修复含水层（Aquifer）密度体素的整数溢出、废弃矿井生成死循环以及越界区块调度断言错误。
* **实体与寻路**：修复了 AI 在世界边界附近游荡时可能引发的 `NegativeArraySizeException`（数组大小为负异常），并优化了实体区块查找逻辑，以确保物品拾取和碰撞检测的准确性。
* **光照引擎**：针对跨越未载入区块边界的光照传播实现了空安全（null-safe）回退机制，从而避免 `NullPointerException`（NPE，空指针异常）。
* **并发保护**：绕过了并行特征生成（parallel feature generation）过程中的线程锁断言检查，确保与第三方世界生成模组（mods）具有广泛的兼容性。

### 🚀 快速入门

安装完成后，请使用管理员指令配置世界边界：

```bash
# 1. 将世界边界扩展至最大物理直径（约 42.9 亿格）
/worldborder set 4294967262

# 2. 传送至 32 位元座标系的极限边缘
/tp @s 2147483600 100 0
```
---
### ✅ 模组状态

* 32 位元座标展开支援（在 ±2.14 × 10^9 范围内稳定运作）
* 修正了 Minecraft 26.3 版本的 `RotatingSectionStorage` 及八叉树（Octree）渲染问题
* 防止伺服器端世界生成、光照计算及实体 AI 相关的崩溃
* 碰撞箱索引优化及战斗/物品拾取逻辑修复
---
### 🔜 开发路线图
* 还原经典 Beta 1.7.3 风格的「边境之地」（Far Lands）噪音生成效果（目前正在微调「起司」状地形及堆叠墙体地形的生成参数）
</details>