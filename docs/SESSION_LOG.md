# F-Droid preparation verification

## 2026-10-15

Installed `fdroidserver 2.4.5` and its Python dependencies in the disposable
container virtualenv `/tmp/comaps-fdroid-venv` using pip. No host-system changes,
private keys or dependencies were added to the repository.

Checks completed:

- Official F-Droid submission/metadata/anti-feature documentation reviewed.
- Public GitHub repository reachable; GitLab submission form redirects to login.
  No GitLab/GitHub API token or logged-in `gh` session is available.
- `fdroid rewritemeta` used on a temporary copy of the proposed recipe.
- `fdroid lint --format de.ii.android.comapsgooglemaps`: exit 0, no findings.
  Temporary validation directory: `/tmp/comaps-fdroid-validation`.
  Category and anti-feature definitions downloaded from fdroiddata `master`;
  icon references removed only from the temporary validation configuration to
  avoid requiring fdroiddata's unrelated icon assets. IDs/definitions retained.
- Clean tracked-source snapshot plus new store metadata built in a fresh `/tmp`
  directory, without `local.properties`, build outputs or signing files:
  `ANDROID_HOME=/opt/android-sdk ANDROID_SDK_ROOT=/opt/android-sdk ./gradlew
  --no-daemon --max-workers=2 test lint assembleRelease`.
- Result: 39 debug and 39 release tests, 0 failures/errors; Android Lint:
  `No issues found.` Unsigned release APK generated successfully.
- `aapt dump badging`: package `de.ii.android.comapsgooglemaps`, version `1.1.0`,
  code `4`, minSdk `23`, targetSdk `35`, expected application label.
- English/German metadata checked against title 50, summary 80, description
  4000 and changelog 500 character limits.
- Original 512px icon prepared; screenshot captured from the installed signed
  v1.1.0 app on Android 15, showing the app's own information screen only.

Not claimed: full F-Droid build-server/CI run, upstream license approval,
anti-feature approval, reproducible-build verification, submitted merge request,
or acceptance/publication by F-Droid.

Submission materials: `FDROID_SUBMISSION.md` and
`../fdroid/de.ii.android.comapsgooglemaps.yml`.
