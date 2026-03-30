package com.kaiandkaro.dealership.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kaiandkaro.dealership.ui.theme.DealershipTheme
import com.kaiandkaro.dealership.ui.viewmodels.AuthViewModel
import com.kaiandkaro.dealership.ui.viewmodels.VehicleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun AddVehicleScreen(
    navController: NavController,
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isAddingFirestore by vehicleViewModel.isAdding.collectAsState()
    val error by vehicleViewModel.error.collectAsState()
    val user by authViewModel.user.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Local loading state to cover both Upload + Firestore
    var isProcessing by remember { mutableStateOf(false) }

    // Observe ViewModel errors
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            vehicleViewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AddVehicleContent(
                isAdding = isProcessing || isAddingFirestore,
                onAddVehicleClick = { name, make, model, year, type, fuel, price, desc, imageUri, docUri ->
                    if (user == null) {
                        scope.launch { snackbarHostState.showSnackbar("You must be logged in to list a vehicle") }
                        return@AddVehicleContent
                    }

                    scope.launch {
                        isProcessing = true
                        try {
                            var finalImageUrl = ""
                            if (imageUri != null) {
                                val bytes = withContext(Dispatchers.IO) {
                                    val inputStream = context.contentResolver.openInputStream(imageUri)
                                    val bitmap = BitmapFactory.decodeStream(inputStream)
                                    val out = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
                                    out.toByteArray()
                                }
                                
                                if (bytes != null) {
                                    val fileName = "img_${UUID.randomUUID()}.jpg"
                                    finalImageUrl = vehicleViewModel.uploadImage(bytes, fileName) ?: ""
                                }
                            }

                            if (imageUri != null && finalImageUrl.isEmpty()) {
                                snackbarHostState.showSnackbar("Image upload failed. Please check your internet.")
                                isProcessing = false
                                return@launch
                            }

                            var finalDocUrl = ""
                            var finalDocName = ""
                            if (docUri != null) {
                                val bytes = withContext(Dispatchers.IO) {
                                    context.contentResolver.openInputStream(docUri)?.use { it.readBytes() }
                                }
                                if (bytes != null) {
                                    val cursor = context.contentResolver.query(docUri, null, null, null, null)
                                    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    cursor?.moveToFirst()
                                    val originalName = if (nameIndex != null && nameIndex != -1) cursor.getString(nameIndex) else "document.pdf"
                                    cursor?.close()
                                    
                                    val fileName = "doc_${UUID.randomUUID()}_$originalName"
                                    finalDocUrl = vehicleViewModel.uploadDocument(bytes, fileName) ?: ""
                                    finalDocName = originalName
                                }
                            }

                            vehicleViewModel.addVehicle(
                                name = name,
                                make = make,
                                model = model,
                                year = year,
                                type = type,
                                fuel = fuel,
                                price = price,
                                description = desc,
                                imageUrl = finalImageUrl,
                                documentUrl = finalDocUrl,
                                documentName = finalDocName,
                                sellerId = user?.uid ?: ""
                            )
                            
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Vehicle listed successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error: ${e.localizedMessage}")
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleContent(
    isAdding: Boolean,
    onAddVehicleClick: (String, String, String, String, String, String, Double, String, Uri?, Uri?) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var fuel by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDocUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    var docName by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val docLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedDocUri = uri
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor?.moveToFirst()
            docName = if (nameIndex != null && nameIndex != -1) cursor.getString(nameIndex) else "Selected Document"
            cursor?.close()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Vehicle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { if (!isAdding) imageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Tap to add vehicle image", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name (e.g. Porsche Cayenne)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isAdding
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Make") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAdding
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAdding
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAdding
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (e.g. SUV)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAdding
                )
            }

            OutlinedTextField(
                value = fuel,
                onValueChange = { fuel = it },
                label = { Text("Fuel Type (e.g. Hybrid)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isAdding
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price ($)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                enabled = !isAdding
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                enabled = !isAdding
            )

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!isAdding) docLauncher.launch("*/*") },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (selectedDocUri != null) Icons.Default.Description else Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = if (selectedDocUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (selectedDocUri != null) docName else "Upload Vehicle Document (PDF/Docs)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedDocUri != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                    if (selectedDocUri != null && !isAdding) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { selectedDocUri = null; docName = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    onAddVehicleClick(name, make, model, year, type, fuel, priceValue, description, selectedImageUri, selectedDocUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isAdding && name.isNotBlank() && price.isNotBlank() && selectedImageUri != null
            ) {
                if (isAdding) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("List Vehicle", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
