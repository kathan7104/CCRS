# Deploy CCRS on Render with Railway MySQL and SMTP

## 1) Create service on Render
1. Push code to GitHub.
2. In Render, create a new `Web Service` from this repo.
3. Use:
- Build Command: `./mvnw -DskipTests clean package`
- Start Command: `java -Dspring.profiles.active=prod -jar target/*.jar`

Render automatically injects `PORT`; app already binds to it.

## 2) Set required environment variables on Render

### Database (Railway MySQL)
- `SPRING_DATASOURCE_URL=jdbc:mysql://<RAILWAY_HOST>:<RAILWAY_PORT>/<DB_NAME>?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true`
- `SPRING_DATASOURCE_USERNAME=<RAILWAY_USER>`
- `SPRING_DATASOURCE_PASSWORD=<RAILWAY_PASSWORD>`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`

### SMTP (email OTP)
- `SPRING_MAIL_HOST=smtp.gmail.com`
- `SPRING_MAIL_PORT=587`
- `SPRING_MAIL_USERNAME=<YOUR_EMAIL>`
- `SPRING_MAIL_PASSWORD=<GMAIL_APP_PASSWORD>`
- `SPRING_MAIL_SMTP_AUTH=true`
- `SPRING_MAIL_SMTP_STARTTLS_ENABLE=true`
- `SPRING_MAIL_SMTP_SSL_TRUST=*`
- `CCRS_OTP_SEND_EMAIL=true`

### Recommended app flags
- `SPRING_PROFILES_ACTIVE=prod`
- `CCRS_OTP_SEND_SMS=false`
- `CCRS_SMS_PROVIDER=mock`

### Upload storage path
Render filesystem is ephemeral on free tier. Uploaded files can be lost on restart.
- Without persistent disk, keep defaults:
  - `CCRS_UPLOAD_BASE_DIR=uploads`
  - `CCRS_UPLOAD_DOCUMENTS_DIR=uploads/documents`
  - `CCRS_UPLOAD_TEACHING_SCHEMA_DIR=uploads/teaching-schemas`
- With Render Disk mounted at `/var/data`:
  - `CCRS_UPLOAD_BASE_DIR=/var/data/uploads`
  - `CCRS_UPLOAD_DOCUMENTS_DIR=/var/data/uploads/documents`
  - `CCRS_UPLOAD_TEACHING_SCHEMA_DIR=/var/data/uploads/teaching-schemas`

## 3) Railway data migration note
If your Railway DB already has all schema + data, app will reuse it.
If it is empty, import your SQL before first production use.

## 4) SMTP validation
After deploy:
1. Register with a real email.
2. Check Render logs for `OTP email sent to <email>`.
3. If mail fails, confirm Gmail App Password and account security settings.

## 5) Final checks
- App opens at Render URL.
- Login/register pages load.
- New user registration stores rows in Railway MySQL.
- OTP mail arrives in inbox.
