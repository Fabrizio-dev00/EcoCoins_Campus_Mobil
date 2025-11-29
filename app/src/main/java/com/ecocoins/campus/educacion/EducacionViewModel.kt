package com.ecocoins.campus.presentation.educacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.CategoriaEducativa
import com.ecocoins.campus.data.model.ContenidoEducativo
import com.ecocoins.campus.data.model.NivelDificultad
import com.ecocoins.campus.data.model.TipoContenido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducacionViewModel @Inject constructor(
    // TODO: Inyectar repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(EducacionUiState())
    val uiState: StateFlow<EducacionUiState> = _uiState.asStateFlow()

    fun loadContenidos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                delay(500)

                // MOCK DATA
                val mockContenidos = listOf(
                    ContenidoEducativo(
                        id = "1",
                        titulo = "Introducción al Reciclaje",
                        descripcion = "Aprende los conceptos básicos del reciclaje y por qué es importante",
                        tipo = TipoContenido.ARTICULO,
                        categoria = CategoriaEducativa.RECICLAJE_BASICO,
                        dificultad = NivelDificultad.PRINCIPIANTE,
                        duracionMinutos = 5,
                        contenido = "",
                        puntosClave = listOf(
                            "El reciclaje reduce la cantidad de residuos",
                            "Ayuda a conservar recursos naturales",
                            "Reduce la contaminación ambiental"
                        ),
                        recompensaEcoCoins = 20,
                        completado = true,
                        fechaPublicacion = "2024-11-01"
                    ),
                    ContenidoEducativo(
                        id = "2",
                        titulo = "Cómo Separar Residuos Correctamente",
                        descripcion = "Guía práctica para separar tus residuos en casa",
                        tipo = TipoContenido.GUIA,
                        categoria = CategoriaEducativa.SEPARACION_RESIDUOS,
                        dificultad = NivelDificultad.PRINCIPIANTE,
                        duracionMinutos = 10,
                        contenido = "",
                        puntosClave = listOf(
                            "Separa plástico, papel, vidrio y metal",
                            "Limpia los envases antes de reciclar",
                            "No mezcles residuos orgánicos con reciclables"
                        ),
                        recompensaEcoCoins = 30,
                        completado = false,
                        fechaPublicacion = "2024-11-05"
                    ),
                    ContenidoEducativo(
                        id = "3",
                        titulo = "El Impacto del Plástico en los Océanos",
                        descripcion = "Descubre cómo el plástico afecta la vida marina",
                        tipo = TipoContenido.VIDEO,
                        categoria = CategoriaEducativa.IMPACTO_AMBIENTAL,
                        dificultad = NivelDificultad.INTERMEDIO,
                        duracionMinutos = 15,
                        contenido = "",
                        puntosClave = listOf(
                            "8 millones de toneladas de plástico llegan al océano cada año",
                            "El plástico tarda hasta 500 años en degradarse",
                            "Afecta a más de 700 especies marinas"
                        ),
                        recompensaEcoCoins = 50,
                        completado = false,
                        fechaPublicacion = "2024-11-10"
                    ),
                    ContenidoEducativo(
                        id = "4",
                        titulo = "Quiz: ¿Cuánto Sabes de Reciclaje?",
                        descripcion = "Pon a prueba tus conocimientos sobre reciclaje",
                        tipo = TipoContenido.QUIZ,
                        categoria = CategoriaEducativa.RECICLAJE_BASICO,
                        dificultad = NivelDificultad.PRINCIPIANTE,
                        duracionMinutos = 10,
                        contenido = "",
                        puntosClave = listOf(
                            "10 preguntas sobre reciclaje",
                            "Obtén feedback inmediato",
                            "Gana EcoCoins por respuestas correctas"
                        ),
                        recompensaEcoCoins = 100,
                        completado = false,
                        fechaPublicacion = "2024-11-15"
                    ),
                    ContenidoEducativo(
                        id = "5",
                        titulo = "Economía Circular: El Futuro del Consumo",
                        descripcion = "Entiende qué es la economía circular y cómo aplicarla",
                        tipo = TipoContenido.ARTICULO,
                        categoria = CategoriaEducativa.ECONOMIA_CIRCULAR,
                        dificultad = NivelDificultad.AVANZADO,
                        duracionMinutos = 20,
                        contenido = "",
                        puntosClave = listOf(
                            "Modelo de producción y consumo sostenible",
                            "Reducir, reutilizar, reciclar",
                            "Minimizar residuos y aprovechar recursos"
                        ),
                        recompensaEcoCoins = 75,
                        completado = false,
                        fechaPublicacion = "2024-11-20"
                    ),
                    ContenidoEducativo(
                        id = "6",
                        titulo = "10 Consejos para Reducir tu Huella de Carbono",
                        descripcion = "Tips prácticos para un estilo de vida más sostenible",
                        tipo = TipoContenido.INFOGRAFIA,
                        categoria = CategoriaEducativa.CONSEJOS_PRACTICOS,
                        dificultad = NivelDificultad.PRINCIPIANTE,
                        duracionMinutos = 5,
                        contenido = "",
                        puntosClave = listOf(
                            "Usa bolsas reutilizables",
                            "Reduce el consumo de plástico de un solo uso",
                            "Compra productos locales",
                            "Ahorra energía en casa"
                        ),
                        recompensaEcoCoins = 40,
                        completado = false,
                        fechaPublicacion = "2024-11-22"
                    )
                )

                val completados = mockContenidos.count { it.completado }
                val ecoCoins = mockContenidos.filter { it.completado }.sumOf { it.recompensaEcoCoins }

                _uiState.update {
                    it.copy(
                        contenidos = mockContenidos,
                        contenidosCompletados = completados,
                        ecoCoinsGanados = ecoCoins,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar contenidos: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun completarContenido(id: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val contenidos = state.contenidos.map {
                    if (it.id == id) it.copy(completado = true) else it
                }
                val completados = contenidos.count { it.completado }
                val ecoCoins = contenidos.filter { it.completado }.sumOf { it.recompensaEcoCoins }

                state.copy(
                    contenidos = contenidos,
                    contenidosCompletados = completados,
                    ecoCoinsGanados = ecoCoins
                )
            }
        }
    }

    fun refresh() {
        loadContenidos()
    }
}

data class EducacionUiState(
    val contenidos: List<ContenidoEducativo> = emptyList(),
    val contenidosCompletados: Int = 0,
    val ecoCoinsGanados: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)