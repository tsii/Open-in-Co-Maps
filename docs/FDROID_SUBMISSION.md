# F-Droid submission

Status: **prepared, not submitted or accepted**. A GitLab account is required to
open the upstream merge request or Request for Packaging (RFP). The repository's
GitHub deploy key does not provide this access.

## App and release

- Name: CoMaps ❤️ Google Maps
- Application ID: `de.ii.android.comapsgooglemaps`
- Source: https://github.com/tsii/Open-in-Co-Maps
- Release: `v1.1.0`, version code `4`
- Minimum Android: 6.0/API 23; target/compile API 35
- Build: JDK 17, Gradle 8.10.2, AGP 8.7.3, Kotlin 2.0.21
- Localized store text/icon: `fastlane/metadata/android/`
- Proposed fdroiddata recipe: `fdroid/de.ii.android.comapsgooglemaps.yml`

F-Droid builds the app from source and normally signs it with its own key.
An F-Droid-signed APK cannot update the manually distributed APK with a different
signing key. Users may need to uninstall the manually installed app first.
No proprietary SDK, Google Play Services dependency, advertising SDK or analytics
SDK is included. CoMaps is free software. Google Maps itself is optional;
generated HTTPS links can also open in a browser.

## Licensing and review notes

Original app code: **BSD-3-Clause**. The adapted CoMaps decoder and its upstream
test vectors retain **Apache-2.0** with attribution. Full license text and notices
are packaged in the APK. The recipe uses Apache-2.0 as the single combined-binary
license identifier, not as a relicensing of the original BSD sources. Please
confirm this classification during packaging review.

**NonFreeNet** is declared: short-link resolution contacts Google and generated
links target the proprietary Google Maps service. Please assess whether
**NonFreeAdd** is additionally applicable to the optional Google Maps integration.
No mandatory proprietary app dependency is claimed. The app is independent and
not endorsed by CoMaps or Google.

## Preferred submission: fdroiddata merge request

1. Sign in to https://gitlab.com and fork https://gitlab.com/fdroid/fdroiddata .
2. Create a branch and copy `fdroid/de.ii.android.comapsgooglemaps.yml` into the
   fork's `metadata/de.ii.android.comapsgooglemaps.yml`.
3. Run the current fdroiddata lint/build CI and resolve any packaging feedback.
4. Open a merge request against fdroid/fdroiddata, title:
   **New app: CoMaps ❤️ Google Maps (de.ii.android.comapsgooglemaps)**.
5. Paste the text below and reference the public release tag. Only maintainers
   can accept the submission; a local build is not proof of F-Droid acceptance.

### Ready-to-paste submission text

Please package CoMaps ❤️ Google Maps, a small bidirectional Android share target.

- Source: https://github.com/tsii/Open-in-Co-Maps
- Tagged release: https://github.com/tsii/Open-in-Co-Maps/tree/v1.1.0
- Application ID: de.ii.android.comapsgooglemaps
- Version: 1.1.0 / 4
- Original license: BSD-3-Clause; adapted decoder: Apache-2.0, with notices.
- Proposed recipe: https://github.com/tsii/Open-in-Co-Maps/blob/v1.1.0/fdroid/de.ii.android.comapsgooglemaps.yml
- Fastlane metadata and icon are included in the tagged sources.
- Anti-feature: NonFreeNet (Google Maps URL resolution / generated links).
  Please assess NonFreeAdd for optional Google Maps integration.
- Google Maps app is optional; CoMaps is free software; no proprietary SDKs.
- Both directions were independently tested on Android 15 with official CoMaps
  and Google Maps apps. Local release builds and unit tests/lint pass.
- Signing keys are private and are not included in the sources.

## Alternative: Request for Packaging

If you do not want to maintain an fdroiddata fork, open
https://gitlab.com/fdroid/rfp/-/issues/new and use the same information above.
RFP review and waiting for a volunteer packager may take longer.

## Verification and limits

See `SESSION_LOG.md` for actual local commands and results. The recipe is validated
with fdroidserver using upstream category/anti-feature definitions. The app is
also built from a clean Git checkout. The full F-Droid build-server environment,
upstream CI, source review and any reproducible-build assessment remain the
responsibility of the F-Droid packaging process; none are claimed completed here.

Official references:
- https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/
- https://f-droid.org/docs/Build_Metadata_Reference/
- https://f-droid.org/docs/Anti-Features/
- https://gitlab.com/fdroid/fdroiddata/-/blob/master/CONTRIBUTING.md
