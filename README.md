# DIC Survey App — Android

**Offline-first Android survey app for Telangana District Industries Centres**

Government of Telangana · Department of Industries & Commerce

---

## Features

- **4 questionnaire types** — DIC/DoI, Industry/Enterprise, Industrial Association, Stakeholder
- **Fully offline** — fill forms with no internet, responses saved in local SQLite database
- **Auto-sync** — when internet returns, all pending responses sync to Supabase automatically
- **Background sync** — WorkManager retries failed syncs automatically
- **CSV export** — share all responses from the app
- **All 33 Telangana districts** pre-loaded in login screen
- **Role-based login** — officer and admin roles
- **No Google Play Store** required — sideload APK directly

---

## Getting the APK

### Option 1 — GitHub Actions (recommended, fully automatic)

1. Push this code to a GitHub repository
2. Go to **Actions** tab — the build starts automatically
3. When complete, go to **Releases** — download `app-debug.apk`
4. Every new push creates a new release automatically

### Option 2 — Build locally

Requirements: Android Studio or JDK 17 + Android SDK

```bash
export ANDROID_HOME=/path/to/android-sdk
./gradlew assembleDebug
# APK appears at: app/build/outputs/apk/debug/app-debug.apk
```

---

## Installing on Android

1. Transfer APK to phone (USB, WhatsApp, Google Drive, etc.)
2. On phone: **Settings → Security → Install Unknown Apps** → allow your browser or file manager
3. Open the APK file and tap **Install**
4. Open **DIC Survey** app

---

## Supabase Backend Setup (free)

### Step 1 — Create free Supabase project

1. Go to [supabase.com](https://supabase.com) → **Start for free**
2. Create new project → note your **Project URL** and **anon/public API key**

### Step 2 — Create database table

In Supabase → **SQL Editor** → run this:

```sql
create table survey_responses (
  id uuid default gen_random_uuid() primary key,
  questionnaire_type text not null,
  district text,
  respondent_name text,
  form_data jsonb,
  status text default 'SUBMITTED',
  officer_id text,
  created_at bigint,
  submitted_at bigint,
  inserted_at timestamp default now()
);

-- Enable Row Level Security
alter table survey_responses enable row level security;

-- Allow all inserts (officers submitting)
create policy "Allow inserts" on survey_responses
  for insert with check (true);

-- Allow reads for authenticated users
create policy "Allow reads" on survey_responses
  for select using (true);
```

### Step 3 — Configure the app

In the app: after login, go to **Menu → Settings** and enter:
- **Supabase URL**: `https://your-project-id.supabase.co`
- **Supabase Key**: your `anon/public` key

Or hardcode in `PrefManager.java` for a locked deployment:
```java
public String getSupabaseUrl() { 
    return "https://YOUR_PROJECT_ID.supabase.co"; 
}
public String getSupabaseKey() { 
    return "YOUR_ANON_KEY"; 
}
```

### Step 4 — View responses

- **Supabase Studio** → Table Editor → `survey_responses` — view/filter/export all submissions
- Or use the **Export CSV** button inside the app itself

---

## Project Structure

```
app/src/main/java/com/dic/survey/
├── activities/
│   ├── SplashActivity.java              — splash screen
│   ├── LoginActivity.java               — officer login + district select
│   ├── MainActivity.java                — dashboard + sync status
│   ├── DICQuestionnaireActivity.java    — DoI/DIC 7-section form
│   ├── EnterpriseQuestionnaireActivity.java  — Industry/Enterprise form
│   ├── AssociationQuestionnaireActivity.java — Association form
│   ├── StakeholderQuestionnaireActivity.java — Stakeholder form
│   └── ResponsesActivity.java           — view/filter/export responses
├── adapters/
│   └── ResponsesAdapter.java            — RecyclerView adapter
├── database/
│   ├── SurveyDatabase.java              — Room SQLite database
│   └── SurveyDao.java                   — all database queries
├── models/
│   └── SurveyResponse.java              — Room entity
├── sync/
│   ├── SyncManager.java                 — Supabase REST sync engine
│   └── SyncWorker.java                  — WorkManager background sync
└── utils/
    ├── PrefManager.java                 — SharedPreferences (login/settings)
    ├── NetworkUtils.java                — online/offline detection
    └── FormDataHelper.java              — JSON form data builder/parser
```

---

## Tech Stack

| Layer | Technology | Cost |
|---|---|---|
| Android app | Java + Android SDK | Free |
| Local database | Room (SQLite) | Free |
| Offline detection | Android ConnectivityManager | Free |
| Background sync | WorkManager | Free |
| Cloud backend | Supabase (Postgres + REST) | Free tier |
| Admin dashboard | Supabase Studio | Free |
| APK build | GitHub Actions | Free |
| APK distribution | GitHub Releases | Free |

---

## Questionnaire Coverage

| Questionnaire | Sections | Key topics |
|---|---|---|
| DIC / DoI | 7 sections | District profile, industrial units, infrastructure, finance, labour, technology, markets |
| Enterprise | 9 sections | Basic details, land, infrastructure, finance, raw materials, labour, technology, markets, sustainability |
| Association | 8 sections | Basic details, land/infra, finance, raw materials, manpower, technology, market access, governance + 14 sector-specific |
| Stakeholder | 6 sections | Line departments (15+), banks, skill institutions, communities, general |

---

## Telangana Districts Covered (33)

Adilabad, Bhadradri Kothagudem, Hanamkonda, Hyderabad, Jagtial, Jangaon,
Jayashankar Bhupalpally, Jogulamba Gadwal, Kamareddy, Karimnagar, Khammam,
Kumuram Bheem, Mahabubabad, Mahabubnagar, Mancherial, Medak, Medchal Malkajgiri,
Mulugu, Nagarkurnool, Nalgonda, Narayanpet, Nirmal, Nizamabad, Peddapalli,
Rajanna Sircilla, Rangareddy, Sangareddy, Siddipet, Suryapet, Vikarabad,
Wanaparthy, Warangal, Yadadri Bhuvanagiri

---

## License

Government of Telangana · Department of Industries & Commerce
