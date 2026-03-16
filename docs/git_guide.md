# 🃏 Git Guide — Splendor Card Game Project

## 1. First-Time Setup

### Go to the home page, click on the green "Code" button at the top right-hand corner

### Under HTTPS, you will see this link
```
https://github.com/raetan2023/cs102-splendor-project.git
```

### Clone the repository
```bash
cd <to your desired directory>
git clone https://github.com/raetan2023/cs102-splendor-project.git
```

### Set your identity (if you haven't already)
```bash
git config --global user.name "Your Name"
git config --global user.email "your@email.com"
```

### Verify the remotes
```bash
git remote -v
# Should show: origin  https://github.com/raetan2023/cs102-splendor-project.git
```

---

## 2. Branch Strategy

We use **two types of branches**:

| Branch | Purpose | Who merges into it |
|--------|----------|--------------------|
| `main` | Stable code only — merge here at milestones when things are working | Everyone, directly |
| `feature/<name>` | Your personal working branch | You merge into `main` when done |

### Branch naming examples
```
feature/gem-logic
feature/noble-tiles
feature/ai-player
feature/board-renderer
fix/turn-validation
fix/config-loader
```

> ⚠️ **Never commit directly to `main`.** Always work on a `feature/` branch and only merge back when your code compiles and works.

---

## 3. Day-to-Day Workflow

### Step 1 — Start from an up-to-date `main`
```bash
git checkout main
git pull origin main
```

### Step 2 — Create your feature branch
```bash
git checkout -b feature/your-feature-name
```

### Step 3 — Make your changes, then stage and commit
```bash
# Stage specific files (preferred)
git add src/com/splendor/model/TokenBank.java

# Or stage everything in the current directory
git add .

# Commit with a descriptive message (see Section 4)
git commit -m "feat(model): add TokenBank with take/return gem logic"
```

### Step 4 — Push your branch to GitHub
```bash
git push origin feature/your-feature-name
```

### Step 5 — Merge into `main` when your feature is ready
```bash
git checkout main
git pull origin main                      # get latest changes first
git merge feature/your-feature-name       # merge your work in
git push origin main                      # push to GitHub
git branch -d feature/your-feature-name  # delete local branch
```

---

## 4. Commit Message Convention

Use this format for all commits:

```
<type>(<scope>): <short description>
```

### Types

| Type | When to use |
|------|-------------|
| `feat` | New feature or functionality |
| `fix` | Bug fix |
| `refactor` | Code restructuring (no behaviour change) |
| `test` | Adding or updating tests |
| `docs` | Documentation updates |
| `chore` | Build scripts, config changes, misc |

### Scope examples (use your package/class name)

```
feat(model): add DevelopmentCard cost field as int[]
feat(engine): implement ActionValidator for PurchaseCard
fix(config): handle missing config.properties gracefully
test(model): add unit tests for Noble visit logic
refactor(view): extract BoardRenderer from GameView
docs(readme): update compile and run instructions
chore(gitignore): exclude .class and IDE config files
```

---

## ✅ Before Merging into `main`

- [ ] Code compiles cleanly (`bash compile.sh` or `mvn clean package`)
- [ ] No `.class` files or IDE config files committed
- [ ] No hardcoded file paths — use `config.properties`
- [ ] Javadoc on all public classes and methods
- [ ] No `TODO` stubs left in the code you're merging

---

## 5. Handling Merge Conflicts

Conflicts happen when two people edit the same lines. Here's how to resolve them:

### Step 1 — Pull the latest `main` into your branch
```bash
git checkout feature/your-feature-name
git pull origin main
```

### Step 2 — Open the conflicting file(s)
Git marks conflicts like this:

```java
<<<<<<< HEAD
// your version
int tokens = player.getWallet().getNumTokens();
=======
// teammate's version
int tokenCount = player.getAssets().countTokens();
>>>>>>> main
```

Edit the file to keep the correct version (or combine both), then remove the conflict markers.

### Step 3 — Stage and commit the resolution
```bash
git add src/com/splendor/player/Player.java
git commit -m "fix: resolve merge conflict in Player token logic"
git push origin feature/your-feature-name
```

> 💡 To avoid conflicts, pull from `main` frequently and keep your feature branches short-lived.

---

## 6. Useful Commands Cheatsheet

```bash
# See what's changed in your working directory
git status

# See your commit history
git log --oneline --graph

# See all branches (local + remote)
git branch -a

# Discard uncommitted changes to a file
git checkout -- src/com/splendor/model/Board.java

# Undo your last commit (keeps changes in working directory)
git reset --soft HEAD~1

# Temporarily stash your changes (to switch branches cleanly)
git stash
git stash pop   # bring them back

# Fetch all remote branches without merging
git fetch origin
```

---

## 🚫 What NOT to Commit

The `.gitignore` should already cover these, but double-check:

- `classes/` or any `*.class` files — compiled output, not source
- `.idea/`, `*.iml`, `.vscode/` — IDE config files
- `.DS_Store` — macOS system file

If you accidentally committed any of these, ping the group chat so we can clean it up

---

*Questions? Ping the team on the group chat or raise a GitHub Issue.*
