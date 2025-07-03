import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.MetalAlloy
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalAlloyDropdown(
    alloys: List<MetalAlloy>,
    selectedAlloyId: Long?,
    onAlloySelected: (Long?) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAlloy = alloys.find { it.id == selectedAlloyId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedAlloy?.name ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.alloy)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .exposedDropdownSize(), // Changed from .menuAnchor()
            isError = isError,
            supportingText = {
                if (errorMessage != null) {
                    Text(errorMessage)
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            alloys.forEach { alloy ->
                DropdownMenuItem(
                    text = { Text(alloy.name) },
                    onClick = {
                        onAlloySelected(alloy.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericDropdown(
    items: List<T>,
    selectedItemId: Long?,
    onItemSelected: (Long?) -> Unit,
    label: String,
    getItemId: (T) -> Long,
    getItemDisplayName: (T) -> String,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = items.find { getItemId(it) == selectedItemId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { getItemDisplayName(it) } ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .exposedDropdownSize(), // Changed from .menuAnchor()
            isError = isError,
            supportingText = {
                if (errorMessage != null) {
                    Text(errorMessage)
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(getItemDisplayName(item)) },
                    onClick = {
                        onItemSelected(getItemId(item))
                        expanded = false
                    }
                )
            }
        }
    }
}