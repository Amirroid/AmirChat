package ir.amirroid.amirchat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.amirroid.amirchat.R


@Composable
fun NumberKeyboard(
    modifier: Modifier = Modifier,
    onNumberClicked: (String) -> Unit,
    onBackSpaceClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..3).forEach {
                NumberKeyBoardButton(number = it, onClick = onNumberClicked)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (4..6).forEach {
                NumberKeyBoardButton(number = it, onClick = onNumberClicked)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (7..9).forEach {
                NumberKeyBoardButton(number = it, onClick = onNumberClicked)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            NumberKeyBoardButton(number = 0, onClick = onNumberClicked)
            NumberKeyBoardButtonIcon(
                icon = R.drawable.baseline_backspace,
                onClick = onBackSpaceClick
            )
        }
    }
}


@Composable
fun RowScope.NumberKeyBoardButton(number: Int, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick.invoke(number.toString()) },
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .height(50.dp)
            .weight(1f),
    ) {
        Text(text = number.toString(), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
    }
}

@Composable
fun RowScope.NumberKeyBoardButtonIcon(icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .height(50.dp)
            .weight(1f)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}