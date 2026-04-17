# Integrasi Forum Diskusi ke Halaman Bacaan Detail

## Deskripsi

Forum diskusi telah berhasil diintegrasikan ke dalam halaman detail bacaan (`bacaan_detail.html`). Pengguna sekarang dapat:
- Melihat semua komentar pada bacaan
- Menambahkan komentar baru
- Membalas komentar yang ada
- Menghapus komentar mereka

## Struktur Integrasi

### File yang Dimodifikasi
- `src/main/resources/templates/bacaan_detail.html` - Menambahkan section forum diskusi

### File yang Digunakan (Sudah Ada)
- `src/main/java/id/ac/ui/cs/advprog/yomubackend/forum/controller/KomentarController.java`
- `src/main/java/id/ac/ui/cs/advprog/yomubackend/forum/model/Komentar.java`
- `src/main/java/id/ac/ui/cs/advprog/yomubackend/forum/service/KomentarService.java`
- `src/main/java/id/ac/ui/cs/advprog/yomubackend/forum/repository/KomentarRepository.java`

## Fitur-Fitur Forum

### 1. Menampilkan Komentar

Halaman akan secara otomatis memuat semua komentar untuk bacaan tersebut melalui endpoint:

```java
GET /api/forum/bacaan/{bacaanId}/komentar
```

Komentar ditampilkan bersama dengan:
- Nama pelajar yang berkomentar
- Isi komentar
- Waktu posting
- Daftar balasan (jika ada)

### 2. Membuat Komentar Baru

Pengguna dapat mengetik komentar di textarea dan klik tombol "Posting Komentar":

```java
POST /api/forum/komentar
{
  "bacaanId": 1,
  "isi": "Komentar pengguna",
  "parentId": null
}
```

### 3. Membalas Komentar

Setiap komentar memiliki tombol "Balas" yang akan membuka form untuk menulis balasan:

```java
POST /api/forum/komentar
{
  "bacaanId": 1,
  "isi": "Balasan pengguna",
  "parentId": [idKomentarAsli]
}
```

### 4. Menghapus Komentar

Pengguna dapat menghapus komentar mereka sendiri dengan klik tombol "Hapus":

```java
DELETE /api/forum/komentar/{komentarId}
```

## Desain UI/UX

### Styling
- **Bacaan Section** Menampilkan judul, konten, dan tombol mulai kuis
- **Forum Section** Terpisah dengan garis border-top untuk visual hierarchy
- **Komentar Item**
  - Background abu-abu dengan border kiri biru
  - Menampilkan nama pelajar, waktu, dan konten
  - Tombol balas dan hapus
- **Reply Item**
  - Border kiri hijau untuk membedakan dari komentar utama
  - Indentasi ke kanan untuk menunjukkan relasi hierarki
  - Display nama pelajar dengan warna berbeda

### Warna dan Tema
- Primary Blue (#007bff untuk links, buttons)
- Success Green (#28a745 untuk reply items)
- Danger Red (#dc3545 untuk delete buttons)
- Background Light gray (#f5f5f5)

## API Endpoints yang Digunakan

Integrasi ini menggunakan endpoint API dari `KomentarController`:

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/api/forum/bacaan/{bacaanId}/komentar` | Ambil semua komentar untuk bacaan |
| POST | `/api/forum/komentar` | Buat komentar/balasan baru |
| DELETE | `/api/forum/komentar/{komentarId}` | Hapus komentar |
| PUT | `/api/forum/komentar/{komentarId}` | Edit komentar (optional) |

## Cara Kerja JavaScript

### Load Komentar
Saat halaman dimuat, function `loadKomentar()` akan fetch data dari API dan menampilkannya.

### Display Komentar
Function `displayKomentar()` akan:
1. Iterasi setiap komentar
2. Buat DOM element untuk setiap komentar
3. Tambahkan balasan sebagai child elements
4. Attach event listeners untuk tombol-tombol

### Form Submissions
- **Komentar baru**: Trigger pada form submit
- **Balasan**: Trigger manual via `submitReply()` function
- **Hapus**: Trigger via `deleteKomentar()` dengan konfirmasi

## Catatan Teknis

1. **Thymeleaf Inline JavaScript**: Menggunakan `th:inline="javascript"` untuk mendapatkan nilai `bacaan.id` dari backend
2. **FETCH API**: Menggunakan Fetch API untuk komunikasi dengan backend (mendukung semua browser modern)
3. **Security**: Pastikan endpoint API memiliki proper authentication/authorization
4. **Error Handling**: Setiap request memiliki error handling dengan feedback ke user

## Testing

Untuk menguji forum diskusi:

1. Buka halaman bacaan `http://localhost:8080/bacaan/1`
2. Scroll ke bawah untuk melihat section "Forum Diskusi"
3. Coba fitur
   - Tambah komentar baru
   - Balas salah satu komentar
   - Hapus komentar (hanya kommentar Anda sendiri)

## Pengembangan Lanjutan (Optional)

- [ ] Tambah pagination untuk komentar yang banyak
- [ ] Tambah likes/voting untuk komentar
- [ ] Notifikasi real-time untuk balasan baru
- [ ] Edit komentar yang sudah dibuat
- [ ] User profile/avatar di komentar
- [ ] Emoji support dan rich text formatting
- [ ] Markdown support untuk konten komentar
