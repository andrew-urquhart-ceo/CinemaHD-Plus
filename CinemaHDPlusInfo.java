/*
 * CinemaHDPlusInfo.java
 * A tiny CLI helper for CinemaHDPlus.com repositories.
 *
 * Features:
 * - Print project info in plain, Markdown, or JSON
 * - Compute SHA-256 checksum for a local file (e.g., APK)
 * - Version flag
 *
 * Usage:
 *   javac CinemaHDPlusInfo.java
 *   java CinemaHDPlusInfo                 # default: markdown
 *   java CinemaHDPlusInfo --plain
 *   java CinemaHDPlusInfo --markdown
 *   java CinemaHDPlusInfo --json
 *   java CinemaHDPlusInfo --check /path/to/CinemaHD.apk
 *   java CinemaHDPlusInfo --version
 *
 * License: MIT
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;

public final class CinemaHDPlusInfo {

    // ---- Static project metadata (edit to keep in sync with your site) ----
    private static final String PROJECT_NAME   = "CinemaHDPlus";
    private static final String DOMAIN         = "https://cinemahdplus.com";
    private static final String TAGLINE        = "Official website for guides, updates, and safe info about Cinema HD APK.";
    private static final String AUTHOR_NAME    = "Andrew Urquhart";
    private static final String AUTHOR_ROLE    = "Staff Software Engineer at FieldView";
    private static final String AUTHOR_EMAIL   = "cinemahdplusapp@gmail.com";
    private static final String YEAR           = "2025";
    private static final String[] KEYWORDS     = new String[] {
            "cinema hd apk", "cinema hd firestick", "cinema hd v3",
            "cinema app download", "cinema hd plus"
    };
    private static final String VERSION        = "1.0.0";

    private CinemaHDPlusInfo() {}

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(renderMarkdown());
            return;
        }

        // Simple args parsing (one flag at a time, except --check path)
        if (has(args, "--plain")) {
            System.out.println(renderPlain());
            return;
        }
        if (has(args, "--markdown")) {
            System.out.println(renderMarkdown());
            return;
        }
        if (has(args, "--json")) {
            System.out.println(renderJson());
            return;
        }
        if (has(args, "--version")) {
            System.out.println(VERSION);
            return;
        }
        int idx = indexOf(args, "--check");
        if (idx >= 0) {
            if (idx + 1 >= args.length) {
                System.err.println("Missing file path after --check");
                System.exit(2);
            }
            String path = args[idx + 1];
            try {
                String sum = sha256Of(path);
                System.out.println(sum + "  " + new File(path).getName());
            } catch (Exception e) {
                System.err.println("Failed to compute SHA-256: " + e.getMessage());
                System.exit(1);
            }
            return;
        }

        // Unknown args -> help
        printHelp();
        System.exit(2);
    }

    private static boolean has(String[] args, String flag) {
        return Arrays.stream(args).anyMatch(flag::equalsIgnoreCase);
    }

    private static int indexOf(String[] args, String flag) {
        for (int i = 0; i < args.length; i++) {
            if (flag.equalsIgnoreCase(args[i])) return i;
        }
        return -1;
    }

    private static void printHelp() {
        System.out.println("CinemaHDPlusInfo " + VERSION);
        System.out.println("Usage:");
        System.out.println("  java CinemaHDPlusInfo [--plain|--markdown|--json]");
        System.out.println("  java CinemaHDPlusInfo --check <filePath>");
        System.out.println("  java CinemaHDPlusInfo --version");
    }

    // ---- Renderers ----

    private static String renderPlain() {
        StringBuilder sb = new StringBuilder();
        sb.append(PROJECT_NAME).append(" — ").append(DOMAIN).append('\n');
        sb.append(TAGLINE).append('\n').append('\n');
        sb.append("Author: ").append(AUTHOR_NAME).append(" (").append(AUTHOR_ROLE).append(")").append('\n');
        sb.append("Email: ").append(AUTHOR_EMAIL).append('\n');
        sb.append("Keywords: ").append(String.join(", ", KEYWORDS)).append('\n');
        sb.append("Disclaimer: CinemaHDPlus is not affiliated with the original Cinema HD developers or any streaming service.")
          .append(" All content is for educational/informational purposes only. Users are responsible for local law compliance.")
          .append('\n');
        sb.append("© ").append(YEAR).append(" ").append(DOMAIN).append('\n');
        return sb.toString();
    }

    private static String renderMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("# 🎬 ").append(PROJECT_NAME).append(" — Official Website").append('\n').append('\n');
        sb.append("**").append(DOMAIN).append("**").append(" — ").append(TAGLINE).append('\n').append('\n');
        sb.append("## Author").append('\n');
        sb.append("- **").append(AUTHOR_NAME).append("** — ").append(AUTHOR_ROLE).append('\n');
        sb.append("- 📧 ").append("[").append(AUTHOR_EMAIL).append("](mailto:").append(AUTHOR_EMAIL).append(")").append('\n').append('\n');

        sb.append("## Features").append('\n');
        sb.append("- Official & verified info for Cinema HD APK\n");
        sb.append("- Installation guides for Firestick, Android TV, Smart TV\n");
        sb.append("- Safety notes and best practices\n");
        sb.append("- SEO-optimized blog posts & FAQs\n");
        sb.append("- Multi-language support\n\n");

        sb.append("## Keywords").append('\n');
        sb.append("`").append(String.join("` • `", KEYWORDS)).append("`").append('\n').append('\n');

        sb.append("> **Disclaimer:** CinemaHDPlus is not affiliated with the original Cinema HD developers or any streaming service. ")
          .append("All content is for educational/informational purposes only. Users are responsible for compliance with local laws.")
          .append('\n').append('\n');

        sb.append("**© ").append(YEAR).append(" ").append(DOMAIN).append(" — All Rights Reserved.**").append('\n');
        return sb.toString();
    }

    private static String renderJson() {
        // Simple manual JSON (no external libs)
        StringBuilder kw = new StringBuilder();
        kw.append("[");
        for (int i = 0; i < KEYWORDS.length; i++) {
            kw.append("\"").append(escape(KEYWORDS[i])).append("\"");
            if (i < KEYWORDS.length - 1) kw.append(",");
        }
        kw.append("]");

        String disclaimer = "CinemaHDPlus is not affiliated with the original Cinema HD developers or any streaming service. "
                + "All content is for educational/informational purposes only. Users are responsible for local law compliance.";

        return "{\n"
                + "  \"project\": \"" + escape(PROJECT_NAME) + "\",\n"
                + "  \"domain\": \"" + escape(DOMAIN) + "\",\n"
                + "  \"tagline\": \"" + escape(TAGLINE) + "\",\n"
                + "  \"author\": {\n"
                + "    \"name\": \"" + escape(AUTHOR_NAME) + "\",\n"
                + "    \"role\": \"" + escape(AUTHOR_ROLE) + "\",\n"
                + "    \"email\": \"" + escape(AUTHOR_EMAIL) + "\"\n"
                + "  },\n"
                + "  \"keywords\": " + kw.toString() + ",\n"
                + "  \"version\": \"" + escape(VERSION) + "\",\n"
                + "  \"year\": \"" + escape(YEAR) + "\",\n"
                + "  \"disclaimer\": \"" + escape(disclaimer) + "\"\n"
                + "}";
    }

    // ---- Utility: SHA-256 ----

    private static String sha256Of(String filePath) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream in = new FileInputStream(filePath)) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                md.update(buf, 0, r);
            }
        }
        byte[] digest = md.digest();
        return toHex(digest);
    }

    private static String toHex(byte[] bytes) {
        final char[] HEX = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(HEX[(b >> 4) & 0xF]).append(HEX[b & 0xF]);
        }
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
