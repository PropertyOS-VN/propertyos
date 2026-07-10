# web-app

FE + BE nhẹ của PropertyOS — Next.js App Router, xử lý auth (Supabase) và CRUD tòa nhà/phòng/hợp đồng trực tiếp qua Supabase, không cần backend riêng cho các thao tác này.

- **Stack**: Next.js (App Router) + React + Tailwind CSS + Framer Motion + HeroUI Pro
- **Auth & DB**: Supabase (Postgres + Auth)
- **Gọi sang**: `apps/billing-service` (Spring Boot) khi cần sinh hóa đơn/tính toán tài chính — xem `src/lib/billing-client.ts`

## Chạy dev

```bash
npm install
cp .env.example .env.local   # điền NEXT_PUBLIC_SUPABASE_URL, NEXT_PUBLIC_SUPABASE_ANON_KEY
npm run dev
```

## Deploy

Deploy trực tiếp lên [Vercel](https://vercel.com) (free Hobby tier), Root Directory = `apps/web-app`. Set 3 biến môi trường ở bước "Environment Variables" của Vercel giống `.env.example`.

## Cấu trúc

```
src/
├── app/
│   ├── page.tsx           # trang chủ
│   ├── layout.tsx
│   └── api/health/         # health check
└── lib/
    ├── supabase/
    │   ├── client.ts        # Supabase client (Client Component)
    │   └── server.ts        # Supabase client (Server Component/Route Handler)
    └── billing-client.ts     # gọi sang billing-service
```

Xem roadmap chi tiết ở `../../docs/ROADMAP.md` (Giai đoạn 1-3, 5 là phần của `web-app`).
