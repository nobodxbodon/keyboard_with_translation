package com.donbrody.customkeyboard.components.keyboard.controllers

import android.view.inputmethod.InputConnection
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

/**
 * Created by Don.Brody on 7/18/18.
 */
open class DefaultKeyboardController(inputConnection: InputConnection):
        KeyboardController(inputConnection) {
    val englishChineseTranslator : FirebaseTranslator
    var buffer = ""

    init {
        val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                .build()
        englishChineseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        englishChineseTranslator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                    addCharacter('Y')
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    // ...
                    addCharacter('N')
                }
    }
    companion object {
        // Default controller character lengths should be set as an attribute of their EditText
        private const val MAX_CHARACTERS: Int = Int.MAX_VALUE
    }

    override fun handleKeyStroke(c: Char) {
        buffer += c
        englishChineseTranslator.translate(buffer)
                .addOnSuccessListener { translatedText ->
                    // Translation successful.
                    clearAll()
                    for (i in translatedText.indices) {
                        addCharacter(translatedText.get(i))
                    }
                }
                .addOnFailureListener { exception ->
                    // Error.
                    // ...
                }

    }

    override fun handleKeyStroke(key: KeyboardController.SpecialKey) {
        when(key) {
            SpecialKey.DELETE -> {
                deleteNextCharacter()
            }
            SpecialKey.BACKSPACE -> {
                deletePreviousCharacter()
            }
            SpecialKey.CLEAR -> {
                clearAll()
            }
            SpecialKey.FORWARD -> {
                moveCursorForwardAction()
            }
            SpecialKey.BACK -> {
                moveCursorBackAction()
            }
            else -> {
                // If you need access to one of the SpecialKey's not listed here, override
                // this method in a child class and implement it there.
                return
            }
        }
    }

    override fun maxCharacters(): Int {
        return MAX_CHARACTERS
    }
}
