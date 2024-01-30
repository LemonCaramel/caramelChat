package moe.caramel.chat.driver.arch.darwin;

import com.sun.jna.Native;
import moe.caramel.chat.Main;
import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.driver.KeyboardStatus;
import moe.caramel.chat.driver.KeyboardStatus.Language;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.gui.screens.Screen;
import java.util.Locale;

/**
 * Darwin Controller
 */
public final class DarwinController implements IController {

    private final Driver_Darwin driver;

    /**
     * Create Darwin Controller
     */
    public DarwinController() {
        ModLogger.log("[Native] Load the Darwin Controller.");
        this.driver = Native.load(Main.copyLibrary("libdarwincocoainput.dylib"), Driver_Darwin.class);
        this.driver.initialize(
            // Info
            (log) -> ModLogger.log("[Native|C] " + log),
            // Error
            (log) -> ModLogger.error("[Native|C] " + log),
            // Debug
            (log) -> ModLogger.debug("[Native|C] " + log)
        );
    }

    @Override
    public IOperator createOperator(final AbstractIMEWrapper wrapper) {
        return new DarwinOperator(this, wrapper);
    }

    @Override
    public void changeFocusedScreen(final Screen screen) {
        if (screen instanceof ScreenController) {
            return;
        }

        this.driver.refreshInstance();
    }

    @Override
    public void setFocus(final boolean focus) {
    }

    /**
     * Gets the Driver
     *
     * @return driver
     */
    public Driver_Darwin getDriver() {
        return driver;
    }

    @Override
    public KeyboardStatus getKeyboardStatus() {
        /*
        I can not handle this!!!!
        https://github.com/minoki/InputSourceSelector
        com.apple.keylayout.ABC (ABC)
        com.apple.inputmethod.Korean.2SetKorean (2-Set Korean)
        com.apple.inputmethod.Korean (Korean)
        com.apple.CharacterPaletteIM (Emoji & Symbols)
        com.apple.PressAndHold (com.apple.PressAndHold)
        com.apple.50onPaletteIM (Japanese Kana Palette)
        com.apple.keylayout.Czech-QWERTY (Czech – QWERTY)
        com.apple.keylayout.Czech (Czech)
        com.apple.keylayout.Estonian (Estonian)
        com.apple.keylayout.Hungarian-QWERTY (Hungarian – QWERTY)
        com.apple.keylayout.Hungarian (Hungarian)
        com.apple.keylayout.Latvian (Latvian)
        com.apple.keylayout.Lithuanian (Lithuanian)
        com.apple.keylayout.PolishPro (Polish)
        com.apple.keylayout.Polish (Polish – QWERTZ)
        com.apple.keylayout.Slovak (Slovak)
        com.apple.keylayout.Slovak-QWERTY (Slovak – QWERTY)
        com.apple.keylayout.Bulgarian-Phonetic (Bulgarian – QWERTY)
        com.apple.keylayout.Bulgarian (Bulgarian – Standard)
        com.apple.keylayout.Byelorussian (Belarusian)
        com.apple.keylayout.Macedonian (Macedonian)
        com.apple.keylayout.Russian-Phonetic (Russian – QWERTY)
        com.apple.keylayout.Russian (Russian)
        com.apple.keylayout.RussianWin (Russian – PC)
        com.apple.keylayout.Serbian (Serbian)
        com.apple.keylayout.Ukrainian-PC (Ukrainian)
        com.apple.keylayout.Ukrainian (Ukrainian – Legacy)
        com.apple.keylayout.Colemak (Colemak)
        com.apple.keylayout.Dvorak-Left (Dvorak – Left-Handed)
        com.apple.keylayout.Dvorak-Right (Dvorak – Right-Handed)
        com.apple.keylayout.Dvorak (Dvorak)
        com.apple.keylayout.DVORAK-QWERTYCMD (Dvorak – QWERTY ⌘)
        com.apple.keylayout.KANA (Kana)
        com.apple.keylayout.ABC-AZERTY (ABC – AZERTY)
        com.apple.keylayout.ABC-QWERTZ (ABC – QWERTZ)
        com.apple.keylayout.Australian (Australian)
        com.apple.keylayout.Austrian (Austrian)
        com.apple.keylayout.Belgian (Belgian)
        com.apple.keylayout.Brazilian-ABNT2 (Brazilian – ABNT2)
        com.apple.keylayout.Brazilian-Pro (Brazilian)
        com.apple.keylayout.Brazilian (Brazilian – Legacy)
        com.apple.keylayout.British-PC (British – PC)
        com.apple.keylayout.British (British)
        com.apple.keylayout.Canadian-CSA (Canadian – CSA)
        com.apple.keylayout.Canadian (Canadian)
        com.apple.keylayout.CanadianFrench-PC (Canadian – PC)
        com.apple.keylayout.Danish (Danish)
        com.apple.keylayout.Dutch (Dutch)
        com.apple.keylayout.Finnish (Finnish)
        com.apple.keylayout.French-PC (French – PC)
        com.apple.keylayout.French-numerical (French – Numerical)
        com.apple.keylayout.French (French)
        com.apple.keylayout.German (German)
        com.apple.keylayout.Irish (Irish)
        com.apple.keylayout.Italian-Pro (Italian)
        com.apple.keylayout.Italian (Italian – QZERTY)
        com.apple.keylayout.Norwegian (Norwegian)
        com.apple.keylayout.Portuguese (Portuguese)
        com.apple.keylayout.Spanish-ISO (Spanish)
        com.apple.keylayout.Spanish (Spanish – Legacy)
        com.apple.keylayout.Swedish-Pro (Swedish)
        com.apple.keylayout.Swedish (Swedish – Legacy)
        com.apple.keylayout.SwissFrench (Swiss French)
        com.apple.keylayout.SwissGerman (Swiss German)
        com.apple.keylayout.Tongan (Tongan)
        com.apple.keylayout.US (U.S.)
        com.apple.keylayout.USInternational-PC (U.S. International – PC)
        com.apple.keylayout.2SetHangul (2-Set Korean)
        com.apple.keylayout.ABC-India (ABC – India)
        com.apple.keylayout.Adlam-QWERTY (Adlam)
        com.apple.keylayout.AfghanDari (Afghan Dari)
        com.apple.keylayout.AfghanPashto (Afghan Pashto)
        com.apple.keylayout.AfghanUzbek (Afghan Uzbek)
        com.apple.keylayout.Akan (Akan)
        com.apple.keylayout.Albanian (Albanian)
        com.apple.keylayout.Anjal (Anjal)
        com.apple.keylayout.Apache (Apache)
        com.apple.keylayout.Arabic-AZERTY (Arabic – AZERTY)
        com.apple.keylayout.Arabic-NorthAfrica (Arabic – 123)
        com.apple.keylayout.Arabic-QWERTY (Arabic – QWERTY)
        com.apple.keylayout.Arabic (Arabic)
        com.apple.keylayout.ArabicPC (Arabic – PC)
        com.apple.keylayout.Armenian-HMQWERTY (Armenian – HM QWERTY)
        com.apple.keylayout.Armenian-WesternQWERTY (Armenian – Western QWERTY)
        com.apple.keylayout.Assamese (Assamese – Standard)
        com.apple.keylayout.Azeri (Azeri)
        com.apple.keylayout.Bangla-QWERTY (Bangla – QWERTY)
        com.apple.keylayout.Bangla (Bangla – Standard)
        com.apple.keylayout.Bodo (Bodo – Standard)
        com.apple.keylayout.CangjieKeyboard (Cangjie)
        com.apple.keylayout.Cherokee-Nation (Cherokee – Nation)
        com.apple.keylayout.Cherokee-QWERTY (Cherokee – QWERTY)
        com.apple.keylayout.Chickasaw (Chickasaw)
        com.apple.keylayout.Choctaw (Choctaw)
        com.apple.keylayout.Chuvash (Chuvash)
        com.apple.keylayout.Croatian (Croatian)
        com.apple.keylayout.Croatian-PC (Croatian – PC)
        com.apple.keylayout.Devanagari-QWERTY (Devanagari – QWERTY)
        com.apple.keylayout.Devanagari (Hindi – Standard)
        com.apple.keylayout.Dhivehi-QWERTY (Dhivehi)
        com.apple.keylayout.Dogri (Dogri – Standard)
        com.apple.keylayout.Dzongkha (Dzongkha)
        com.apple.keylayout.Faroese (Faroese)
        com.apple.keylayout.FinnishExtended (Finnish – Extended)
        com.apple.keylayout.FinnishSami-PC (Finnish Sámi – PC)
        com.apple.keylayout.Geez-QWERTY (Geʽez)
        com.apple.keylayout.Georgian-QWERTY (Georgian – QWERTY)
        com.apple.keylayout.German-DIN-2137 (German – Standard)
        com.apple.keylayout.Greek (Greek)
        com.apple.keylayout.GreekPolytonic (Greek – Polytonic)
        com.apple.keylayout.Gujarati-QWERTY (Gujarati – QWERTY)
        com.apple.keylayout.Gujarati (Gujarati – Standard)
        com.apple.keylayout.Gurmukhi-QWERTY (Gurmukhi – QWERTY)
        com.apple.keylayout.Gurmukhi (Gurmukhi – Standard)
        com.apple.keylayout.Hanifi-Rohingya-QWERTY (Hanifi Rohingya)
        com.apple.keylayout.Hausa (Hausa)
        com.apple.keylayout.Hawaiian (Hawaiian)
        com.apple.keylayout.Hebrew-QWERTY (Hebrew – QWERTY)
        com.apple.keylayout.Hebrew (Hebrew)
        com.apple.keylayout.Hebrew-PC (Hebrew – PC)
        com.apple.keylayout.Icelandic (Icelandic)
        com.apple.keylayout.Igbo (Igbo)
        com.apple.keylayout.Ingush (Ingush)
        com.apple.keylayout.Inuktitut-Nattilik (Inuktitut – Nattilik)
        com.apple.keylayout.Inuktitut-Nunavut (Inuktitut – Nunavut)
        com.apple.keylayout.Inuktitut-Nutaaq (Inuktitut – Nutaaq)
        com.apple.keylayout.Inuktitut-QWERTY (Inuktitut – QWERTY)
        com.apple.keylayout.InuttitutNunavik (Inuktitut – Nunavik)
        com.apple.keylayout.IrishExtended (Irish – Extended)
        com.apple.keylayout.Jawi-QWERTY (Jawi)
        com.apple.keylayout.Kabyle-AZERTY (Kabyle – AZERTY)
        com.apple.keylayout.Kabyle-QWERTY (Kabyle – QWERTY)
        com.apple.keylayout.Kannada-QWERTY (Kannada – QWERTY)
        com.apple.keylayout.Kannada (Kannada – Standard)
        com.apple.keylayout.Kashmiri-Devanagari (Kashmiri (Devanagari) – Standard)
        com.apple.keylayout.Kazakh (Kazakh)
        com.apple.keylayout.Khmer (Khmer)
        com.apple.keylayout.Konkani (Konkani – Standard)
        com.apple.keylayout.Kurdish-Kurmanji (Kurmanji Kurdish)
        com.apple.keylayout.Kurdish-Sorani (Sorani Kurdish)
        com.apple.keylayout.Kyrgyz-Cyrillic (Kyrgyz)
        com.apple.keylayout.Lao (Lao)
        com.apple.keylayout.LatinAmerican (Latin American)
        com.apple.keylayout.Maithili (Maithili – Standard)
        com.apple.keylayout.Malayalam-QWERTY (Malayalam – QWERTY)
        com.apple.keylayout.Malayalam (Malayalam – Standard)
        com.apple.keylayout.Maltese (Maltese)
        com.apple.keylayout.Mandaic-Arabic (Mandaic – Arabic)
        com.apple.keylayout.Mandaic-QWERTY (Mandaic – QWERTY)
        com.apple.keylayout.Manipuri-Bengali (Manipuri (Bengali) – Standard)
        com.apple.keylayout.Manipuri-MeeteiMayek (Manipuri (Meetei Mayek))
        com.apple.keylayout.Maori (Māori)
        com.apple.keylayout.Marathi (Marathi – Standard)
        com.apple.keylayout.Mikmaw (Mi’kmaq)
        com.apple.keylayout.Mongolian-Cyrillic (Mongolian)
        com.apple.keylayout.Myanmar-QWERTY (Myanmar – QWERTY)
        com.apple.keylayout.Myanmar (Myanmar)
        com.apple.keylayout.NKo-QWERTY (N’Ko – QWERTY)
        com.apple.keylayout.NKo (N’Ko)
        com.apple.keylayout.Navajo (Navajo)
        com.apple.keylayout.Nepali-IS16350 (Nepali – Standard (India))
        com.apple.keylayout.Nepali (Nepali – Remington)
        com.apple.keylayout.NorwegianExtended (Norwegian – Extended)
        com.apple.keylayout.NorwegianSami-PC (Norwegian Sámi – PC)
        com.apple.keylayout.Oriya-QWERTY (Odia – QWERTY)
        com.apple.keylayout.Oriya (Odia – Standard)
        com.apple.keylayout.Osage-QWERTY (Osage – QWERTY)
        com.apple.keylayout.Pahawh-Hmong (Hmong (Pahawh))
        com.apple.keylayout.Persian-QWERTY (Persian – QWERTY)
        com.apple.keylayout.Persian (Persian – Legacy)
        com.apple.keylayout.Persian-ISIRI2901 (Persian – Standard)
        com.apple.keylayout.Rejang-QWERTY (Rejang – QWERTY)
        com.apple.keylayout.Romanian-Standard (Romanian – Standard)
        com.apple.keylayout.Romanian (Romanian)
        com.apple.keylayout.Sami-PC (Sámi – PC)
        com.apple.keylayout.InariSami (Inari Sámi)
        com.apple.keylayout.JulevSami (Lule Sámi)
        com.apple.keylayout.KildinSami (Kildin Sámi)
        com.apple.keylayout.NorthernSami (Northern Sámi)
        com.apple.keylayout.PiteSami (Pite Sámi)
        com.apple.keylayout.SkoltSami (Skolt Sámi)
        com.apple.keylayout.SouthernSami (Southern Sámi)
        com.apple.keylayout.UmeSami (Ume Sámi)
        com.apple.keylayout.Samoan (Samoan)
        com.apple.keylayout.Sanskrit (Sanskrit – Standard)
        com.apple.keylayout.Santali-Devanagari (Santali (Devanagari) – Standard)
        com.apple.keylayout.Santali-OlChiki (Santali (Ol Chiki))
        com.apple.keylayout.Serbian-Latin (Serbian (Latin))
        com.apple.keylayout.Sindhi-Devanagari (Sindhi (Devanagari) – Standard)
        com.apple.keylayout.Sindhi (Sindhi)
        com.apple.keylayout.Sinhala-QWERTY (Sinhala – QWERTY)
        com.apple.keylayout.Sinhala (Sinhala)
        com.apple.keylayout.Slovenian (Slovenian)
        com.apple.keylayout.SwedishSami-PC (Swedish Sámi – PC)
        com.apple.keylayout.Syriac-Arabic (Syriac – Arabic)
        com.apple.keylayout.Syriac-QWERTY (Syriac – QWERTY)
        com.apple.keylayout.Tajik-Cyrillic (Tajik (Cyrillic))
        com.apple.keylayout.Tamil99 (Tamil99)
        com.apple.keylayout.Telugu-QWERTY (Telugu – QWERTY)
        com.apple.keylayout.Telugu (Telugu – Standard)
        com.apple.keylayout.Thai-PattaChote (Thai – Pattachote)
        com.apple.keylayout.Thai (Thai)
        com.apple.keylayout.TibetanOtaniUS (Tibetan – Otani)
        com.apple.keylayout.Tibetan-QWERTY (Tibetan – QWERTY)
        com.apple.keylayout.Tibetan-Wylie (Tibetan – Wylie)
        com.apple.keylayout.Tifinagh-AZERTY (Tifinagh)
        com.apple.keylayout.Transliteration-bn (Bangla – Transliteration)
        com.apple.keylayout.Transliteration-gu (Gujarati – Transliteration)
        com.apple.keylayout.Transliteration-hi (Hindi – Transliteration)
        com.apple.keylayout.Transliteration-kn (Kannada – Transliteration)
        com.apple.keylayout.Transliteration-ml (Malayalam – Transliteration)
        com.apple.keylayout.Transliteration-mr (Marathi – Transliteration)
        com.apple.keylayout.Transliteration-pa (Punjabi – Transliteration)
        com.apple.keylayout.Transliteration-ta (Tamil – Transliteration)
        com.apple.keylayout.Transliteration-te (Telugu – Transliteration)
        com.apple.keylayout.Transliteration-ur (Urdu – Transliteration)
        com.apple.keylayout.Turkish-QWERTY-PC (Turkish Q)
        com.apple.keylayout.Turkish-QWERTY (Turkish Q – Legacy)
        com.apple.keylayout.Turkish-Standard (Turkish F)
        com.apple.keylayout.Turkish (Turkish F – Legacy)
        com.apple.keylayout.Turkmen (Turkmen)
        com.apple.keylayout.USExtended (ABC – Extended)
        com.apple.keylayout.Ukrainian-QWERTY (Ukrainian – QWERTY)
        com.apple.keylayout.UnicodeHexInput (Unicode Hex Input)
        com.apple.keylayout.Urdu (Urdu)
        com.apple.keylayout.Uyghur (Uyghur)
        com.apple.keylayout.Uzbek-Cyrillic (Uzbek (Cyrillic))
        com.apple.keylayout.Vietnamese (Vietnamese)
        com.apple.keylayout.Wancho-QWERTY (Wancho – QWERTY)
        com.apple.keylayout.Welsh (Welsh)
        com.apple.keylayout.Wolastoqey (Wolastoqey)
        com.apple.keylayout.Yiddish-QWERTY (Yiddish – QWERTY)
        com.apple.keylayout.Yoruba (Yoruba)
        com.apple.keylayout.ZhuyinBopomofo (Zhuyin)
        com.apple.keylayout.GJCRomaja (GongjinCheong Romaja)
        com.apple.keylayout.390Hangul (3-Set Korean (390))
        com.apple.keylayout.HNCRomaja (HNC Romaja)
        com.apple.keylayout.3SetHangul (3-Set Korean)
        com.apple.keylayout.PinyinKeyboard (Pinyin - Simplified)
        com.apple.keylayout.WubihuaKeyboard (Stroke - Simplified)
        com.apple.keylayout.TraditionalWubihuaKeyboard (Stroke - Cantonese)
        com.apple.keylayout.TraditionalWubihuaKeyboard (Stroke - Traditional)
        com.apple.keylayout.ZhuyinEten (Zhuyin Eten - Traditional)
        com.apple.keylayout.TraditionalPinyinKeyboard (Pinyin - Traditional)
        com.apple.keylayout.WubihuaKeyboard (WubihuaKeyboard)
        com.apple.inputmethod.PluginIM (PluginIM)
        com.apple.SyntheticRomanMode (영어)
        com.apple.inputmethod.VietnameseIM (Vietnamese)
        com.apple.inputmethod.Ainu (Ainu)
        com.apple.inputmethod.SCIM (Chinese, Simplified)
        com.apple.inputmethod.ChineseHandwriting (Trackpad Handwriting)
        com.apple.inputmethod.Kotoeri.KanaTyping (Japanese – Kana)
        com.apple.inputmethod.EmojiFunctionRowItem (EmojiFunctionRowIM_Extension)
        com.apple.inputmethod.AssistiveControl (AssistiveControl)
        com.apple.inputmethod.ironwood (Dictation)
        com.apple.inputmethod.Kotoeri.RomajiTyping (Japanese – Romaji)
        com.apple.inputmethod.TYIM (Cantonese, Traditional)
        com.apple.inputmethod.Tamil (Tamil)
        com.apple.inputmethod.TransliterationIM (Transliteration)
        com.apple.inputmethod.TCIM (Chinese, Traditional)
        com.apple.inputmethod.VietnameseIM.VietnameseVNI (VNI)
        com.apple.inputmethod.VietnameseIM.VietnameseVIQR (VIQR)
        com.apple.inputmethod.VietnameseIM.VietnameseSimpleTelex (Simple Telex)
        com.apple.inputmethod.VietnameseIM.VietnameseTelex (Telex)
        com.apple.inputmethod.Korean.390Sebulshik (3-Set Korean (390))
        com.apple.inputmethod.Korean.3SetKorean (3-Set Korean)
        com.apple.inputmethod.Korean.GongjinCheongRomaja (GongjinCheong Romaja)
        com.apple.inputmethod.Korean.HNCRomaja (HNC Romaja)
        com.apple.inputmethod.AinuIM.Ainu (Ainu)
        com.apple.inputmethod.SCIM.ITABC (Pinyin - Simplified)
        com.apple.inputmethod.SCIM.Shuangpin (Shuangpin - Simplified)
        com.apple.inputmethod.SCIM.WBX (Wubi - Simplified)
        com.apple.inputmethod.SCIM.WBH (Stroke - Simplified)
        com.apple.inputmethod.Kotoeri.KanaTyping.Japanese.Katakana (Katakana)
        com.apple.inputmethod.Kotoeri.KanaTyping.Japanese.HalfWidthKana (Half-width Katakana)
        com.apple.inputmethod.Kotoeri.KanaTyping.Roman (Romaji)
        com.apple.inputmethod.Kotoeri.KanaTyping.Japanese.FullWidthRoman (Full-width Romaji)
        com.apple.inputmethod.Kotoeri.KanaTyping.Japanese (Hiragana)
        com.apple.inputmethod.Kotoeri.RomajiTyping.Japanese.Katakana (Katakana)
        com.apple.inputmethod.Kotoeri.RomajiTyping.Japanese.HalfWidthKana (Half-width Katakana)
        com.apple.inputmethod.Kotoeri.RomajiTyping.Roman (Romaji)
        com.apple.inputmethod.Kotoeri.RomajiTyping.Japanese.FullWidthRoman (Full-width Romaji)
        com.apple.inputmethod.Kotoeri.RomajiTyping.Japanese (Hiragana)
        com.apple.inputmethod.TYIM.Stroke (Stroke - Cantonese)
        com.apple.inputmethod.TYIM.Sucheng (Sucheng - Cantonese)
        com.apple.inputmethod.TYIM.Cangjie (Cangjie - Cantonese)
        com.apple.inputmethod.TYIM.Phonetic (Phonetic - Cantonese)
        com.apple.inputmethod.Tamil.AnjalIM (Tamil Anjal)
        com.apple.inputmethod.Tamil.Tamil99 (Tamil 99)
        com.apple.inputmethod.TransliterationIM.ml (Malayalam – Transliteration)
        com.apple.inputmethod.TransliterationIM.mr (Marathi – Transliteration)
        com.apple.inputmethod.TransliterationIM.pa (Punjabi – Transliteration)
        com.apple.inputmethod.TransliterationIM.hi (Hindi – Transliteration)
        com.apple.inputmethod.TransliterationIM.te (Telugu – Transliteration)
        com.apple.inputmethod.TransliterationIM.ur (Urdu – Transliteration)
        com.apple.inputmethod.TransliterationIM.bn (Bangla – Transliteration)
        com.apple.inputmethod.TransliterationIM.kn (Kannada – Transliteration)
        com.apple.inputmethod.TransliterationIM.gu (Gujarati – Transliteration)
        com.apple.inputmethod.TransliterationIM.ta (Tamil – Transliteration)
        com.apple.inputmethod.TCIM.WBH (Stroke - Traditional)
        com.apple.inputmethod.TCIM.Zhuyin (Zhuyin - Traditional)
        com.apple.inputmethod.TCIM.Cangjie (Cangjie - Traditional)
        com.apple.inputmethod.TCIM.ZhuyinEten (Zhuyin Eten - Traditional)
        com.apple.inputmethod.TCIM.Jianyi (Sucheng - Traditional)
        com.apple.inputmethod.TCIM.Pinyin (Pinyin - Traditional)
        com.apple.inputmethod.TCIM.Shuangpin (Shuangpin - Traditional)
        */

        final String imeSource = driver.getStatus();
        if (imeSource == null) {
            return null;
        }

        final Language source = DarwinController.parseSourceFromString(imeSource.toLowerCase(Locale.ENGLISH));
        return new KeyboardStatus(source, true);
    }

    private static Language parseSourceFromString(final String imeSource) {
        if (imeSource.contains("abc")) {
            return Language.ENGLISH;
        } else if (imeSource.contains("korean")) {
            return Language.KOREAN;
        } else if (imeSource.contains("kotoeri") || imeSource.contains("japanese")) {
            return Language.JAPANESE;
        } else if (imeSource.contains("scim")) {
            return Language.CHINESE_SIMPLIFIED;
        } else if (imeSource.contains("tcim")) {
            return Language.CHINESE_TRADITIONAL;
        } else {
            return Language.OTHER;
        }
    }
}
