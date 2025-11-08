package ui;

import java.util.LinkedHashMap;
import java.util.Map;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_BISHOP;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_KNIGHT;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_QUEEN;
import static ui.EscapeSequences.BLACK_ROOK;
import static ui.EscapeSequences.EMPTY;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.WHITE_KING;

public class BoardTesting {

    // Common visible and invisible “blank” characters
    public static final Map<String, String> BLANK_MAP = new LinkedHashMap<>() {{
        // Standard spaces (White_Space = yes)
        put("SPACE",                "\u0020"); // normal space [web:55][web:61]
        put("NBSP",                 "\u00A0"); // no-break space [web:55][web:61]
        put("OGHAM_SPACE_MARK",     "\u1680"); // ogham space mark [web:55][web:61]
        put("EN_QUAD",              "\u2000"); // canonical to EN_SPACE [web:55]
        put("EM_QUAD",              "\u2001"); // canonical to EM_SPACE [web:55]
        put("EN_SPACE",             "\u2002"); // en space [web:55]
        put("EM_SPACE",             "\u2003"); // em space [web:55]
        put("THREE_PER_EM",         "\u2004"); // 1/3 em [web:55]
        put("FOUR_PER_EM",          "\u2005"); // 1/4 em [web:55]
        put("SIX_PER_EM",           "\u2006"); // 1/6 em [web:55]
        put("FIGURE_SPACE",         "\u2007"); // digit-width space [web:55]
        put("PUNCTUATION_SPACE",    "\u2008"); // punctuation-width [web:55]
        put("THIN_SPACE",           "\u2009"); // thin space [web:55]
        put("HAIR_SPACE",           "\u200A"); // hair space [web:55]
        put("NARROW_NBSP",          "\u202F"); // narrow no-break [web:55]
        put("MEDIUM_MATH_SPACE",    "\u205F"); // medium mathematical space [web:55]
        put("IDEOGRAPHIC_SPACE",    "\u3000"); // fullwidth CJK space [web:55][web:61]

        // Zero-width / invisible (not whitespace but visually blank)
        put("ZERO_WIDTH_SPACE",     "\u200B"); // ZWSP soft break [web:57][web:59]
        put("ZWNJ",                 "\u200C"); // zero width non-joiner [web:59]
        put("ZWJ",                  "\u200D"); // zero width joiner [web:59]
        put("WORD_JOINER",          "\u2060"); // zero width non-breaking [web:59]
        put("LRM",                  "\u200E"); // left-to-right mark [web:57]
        put("RLM",                  "\u200F"); // right-to-left mark [web:57]
        put("BOM_ZWNBSP",           "\uFEFF"); // BOM; deprecated as space [web:54][web:59]

        // “Blank-like” but not whitespace per Unicode
        put("BRAILLE_BLANK",        "\u2800"); // Braille blank (font-dependent) [web:55][web:54]
        put("HANGUL_FILLER",        "\u3164"); // Hangul filler; often renders blank [web:58]
    }};


    public static void main(String[] args) {
//        for (var key : BLANK_MAP.keySet()) {
//            System.out.printf("%s%s%s%s   %s", SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, BLANK_MAP.get(key), RESET_BG_COLOR, key);
//            System.out.print("\n");
//        }

        printBoard("white");
    }
    public static void printBoard(String color) {
        String[] layer1 = {"   ", " \u2009a ", " \u2007b ", " \u2004c ", " \u2007d ", " \u2004e ", " \u2007f ", " \u2004g ", " \u2007h ", "   \u200A"};
        String[] leftNumbers = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ", "   "};
        String[] rightNumbers = {"   ", " 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};

        String[] whitePieceIndex = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] blackPieceIndex = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};

        for (var part : layer1) {
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE, part, RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        for (int x = 0; x < 8; x++) {
            System.out.print("\n");
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE, leftNumbers[x], RESET_TEXT_COLOR, RESET_BG_COLOR);
            for (int y = 0; y < 8; y++) {
                if (x == 0 && (x + y) % 2 == 0) {
                    System.out.printf("%s%s%s%s%s", SET_BG_COLOR_SAND, SET_PIECE_COLOR_BLACK, blackPieceIndex[y], HAIRSPACE, RESET_BG_COLOR);
                } else if (x == 0) {
                    System.out.printf("%s%s%s%s", SET_BG_COLOR_CHARCOAL, SET_PIECE_COLOR_BLACK, blackPieceIndex[y], RESET_BG_COLOR);
                } else if (x == 1 && (x + y) % 2 == 0) {
                    System.out.printf("%s%s%s%s%s", SET_BG_COLOR_SAND, SET_PIECE_COLOR_BLACK, BLACK_PAWN, HAIRSPACE, RESET_BG_COLOR);
                } else if (x == 1 && (x + y) % 2 != 0) {
                    System.out.printf("%s%s%s%s", SET_BG_COLOR_CHARCOAL, SET_PIECE_COLOR_BLACK, BLACK_PAWN, RESET_BG_COLOR);
                } else if ((x + y) % 2 == 0) {
                    System.out.printf("%s%s%s", SET_BG_COLOR_SAND, EMPTY, RESET_BG_COLOR);
                } else {
                    System.out.printf("%s%s%s", SET_BG_COLOR_CHARCOAL, EMPTY, RESET_BG_COLOR);
                }
            }
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE, rightNumbers[x], RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        System.out.print("\n");
        for (var part : layer1) {
            System.out.printf("%s%s%s%s%s", SET_BG_COLOR_BORDER, SET_TEXT_COLOR_WHITE, part, RESET_TEXT_COLOR, RESET_BG_COLOR);
        }
        System.out.print("\n");
    }
}
