package com.ecocoins.campus.presentation.soporte

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.ui.components.EmptyState
import com.ecocoins.campus.ui.components.LoadingState
import com.ecocoins.campus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    onNavigateBack: () -> Unit,
    viewModel: SoporteViewModel = hiltViewModel()
) {
    val faqs by viewModel.faqs.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var selectedCategory by remember { mutableStateOf("Todos") }
    val categorias = listOf("Todos", "General", "Reciclaje", "Recompensas", "Cuenta", "Técnico")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preguntas Frecuentes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(message = "Cargando FAQs...")
            }
            faqs.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.Help,
                    title = "Sin preguntas frecuentes",
                    message = "No hay FAQs disponibles"
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Filtros por categoría
                    ScrollableTabRow(
                        selectedTabIndex = categorias.indexOf(selectedCategory),
                        edgePadding = 16.dp,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        categorias.forEach { categoria ->
                            Tab(
                                selected = selectedCategory == categoria,
                                onClick = {
                                    selectedCategory = categoria
                                    if (categoria == "Todos") {
                                        viewModel.loadFAQs()
                                    } else {
                                        viewModel.loadFAQs(categoria)
                                    }
                                },
                                text = { Text(categoria) }
                            )
                        }
                    }

                    // Lista de FAQs
                    val faqsFiltrados = if (selectedCategory == "Todos") {
                        faqs
                    } else {
                        faqs.filter { it.categoria == selectedCategory }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(faqsFiltrados) { faq ->
                            FAQCard(
                                faq = faq,
                                onMarcarUtil = { viewModel.marcarFAQUtil(faq.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FAQCard(
    faq: FAQ,
    onMarcarUtil: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pregunta (siempre visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = "Pregunta",
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = faq.pregunta,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Respuesta (expandible)
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 52.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Divider(modifier = Modifier.padding(bottom = 12.dp))

                    Text(
                        text = faq.respuesta,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Categoría
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcoGreenPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = faq.categoria,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = EcoGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // ¿Te fue útil?
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "¿Te fue útil?",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onMarcarUtil,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (faq.util) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                    contentDescription = "Útil",
                                    tint = if (faq.util) EcoGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}