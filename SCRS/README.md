# CCRS (SCRS)

Simple Spring Boot project for user registration, login, OTP verification, and password reset.

## Build and run (Windows)

1. Build:

```bash
mvnw.cmd clean package
```

2. Run:

```bash
mvnw.cmd spring-boot:run
```

Or run the generated jar:

```bash
java -jar target/*.jar
```

## GitHub push (one-time setup)

Replace `main` with your branch name if different.

```bash
# set your user
git config user.name "kathan7104"

# initialize repo if not already
git init

# add remote (only if not already added)
git remote add origin https://github.com/kathan7104/CCRS.git

# stage changes
git add .

# commit
git commit -m "Add project and comments"

# push to GitHub (force if you need to overwrite remote main)
git branch -M main
git push -u origin main
```

If you have two-factor auth, use a personal access token instead of a password when prompted.

## What I changed

- Added this README.md
- Added simple English comments to Java sources (explain file purpose and main methods)

If you want, I can also create a small `CONTRIBUTING.md` or make the commit and push for you if you provide a GitHub token.