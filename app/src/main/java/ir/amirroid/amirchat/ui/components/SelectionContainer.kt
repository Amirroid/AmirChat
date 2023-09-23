package ir.amirroid.amirchat.ui.components

import androidx.compose.runtime.Composable

@Composable
fun SelectionContainer(enabled:Boolean, content:@Composable ()->Unit) {
    if (enabled){
        androidx.compose.foundation.text.selection.SelectionContainer(content = content)
    }else{
        content.invoke()
    }
}