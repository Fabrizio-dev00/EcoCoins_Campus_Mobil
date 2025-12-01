package com.ecocoins.campus.presentation.educacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.repository.EducacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducacionViewModel @Inject constructor(
    private val educacionRepository: EducacionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _contenidos = MutableLiveData<Resource<List<ContenidoEducativo>>>()
    val contenidos: LiveData<Resource<List<ContenidoEducativo>>> = _contenidos

    private val _contenidoSeleccionado = MutableLiveData<Resource<ContenidoEducativo>?>()
    val contenidoSeleccionado: LiveData<Resource<ContenidoEducativo>?> = _contenidoSeleccionado

    private val _progreso = MutableLiveData<Resource<ProgresoEducativo>?>()
    val progreso: LiveData<Resource<ProgresoEducativo>?> = _progreso

    private val _categorias = MutableLiveData<Resource<List<CategoriaEducativa>>>()
    val categorias: LiveData<Resource<List<CategoriaEducativa>>> = _categorias

    private val _quiz = MutableLiveData<Resource<Quiz>?>()
    val quiz: LiveData<Resource<Quiz>?> = _quiz

    // ✅ CORREGIDO: Ahora es nullable
    private val _resultadoQuiz = MutableLiveData<Resource<ResultadoQuiz>?>()
    val resultadoQuiz: LiveData<Resource<ResultadoQuiz>?> = _resultadoQuiz

    private val _completarContenido = MutableLiveData<Resource<Map<String, Any>>?>()
    val completarContenido: LiveData<Resource<Map<String, Any>>?> = _completarContenido

    private val _filtroCategoria = MutableLiveData<String?>()
    val filtroCategoria: LiveData<String?> = _filtroCategoria

    private val _filtroTipo = MutableLiveData<String?>()
    val filtroTipo: LiveData<String?> = _filtroTipo

    init {
        cargarContenidos()
        cargarProgreso()
        cargarCategorias()
    }

    fun cargarContenidos(categoria: String? = null, tipo: String? = null) {
        viewModelScope.launch {
            _contenidos.value = Resource.Loading()
            _filtroCategoria.value = categoria
            _filtroTipo.value = tipo

            when (val result = educacionRepository.obtenerContenidos(categoria, tipo)) {
                is Resource.Success -> {
                    _contenidos.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _contenidos.value = Resource.Error(
                        result.message ?: "Error al cargar contenidos"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarContenido(contenidoId: String) {
        viewModelScope.launch {
            _contenidoSeleccionado.value = Resource.Loading()

            when (val result = educacionRepository.obtenerContenido(contenidoId)) {
                is Resource.Success -> {
                    // ✅ MEJORADO: Manejo seguro de null
                    result.data?.let {
                        _contenidoSeleccionado.value = Resource.Success(it)
                    } ?: run {
                        _contenidoSeleccionado.value = Resource.Error("Contenido no encontrado")
                    }
                }
                is Resource.Error -> {
                    _contenidoSeleccionado.value = Resource.Error(
                        result.message ?: "Error al cargar contenido"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarProgreso() {
        viewModelScope.launch {
            _progreso.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = educacionRepository.obtenerProgreso(usuarioId)) {
                is Resource.Success -> {
                    result.data?.let {
                        _progreso.value = Resource.Success(it)
                    } ?: run {
                        _progreso.value = Resource.Error("No se pudo obtener el progreso")
                    }
                }
                is Resource.Error -> {
                    _progreso.value = Resource.Error(
                        result.message ?: "Error al cargar progreso"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun completarContenido(contenidoId: String) {
        viewModelScope.launch {
            _completarContenido.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = educacionRepository.completarContenido(usuarioId, contenidoId)) {
                is Resource.Success -> {
                    result.data?.let {
                        _completarContenido.value = Resource.Success(it)
                    }
                    cargarProgreso()
                    cargarContenidos(_filtroCategoria.value, _filtroTipo.value)
                }
                is Resource.Error -> {
                    _completarContenido.value = Resource.Error(
                        result.message ?: "Error al completar contenido"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarQuiz(quizId: String) {
        viewModelScope.launch {
            _quiz.value = Resource.Loading()

            when (val result = educacionRepository.obtenerQuiz(quizId)) {
                is Resource.Success -> {
                    result.data?.let {
                        _quiz.value = Resource.Success(it)
                    } ?: run {
                        _quiz.value = Resource.Error("Quiz no encontrado")
                    }
                }
                is Resource.Error -> {
                    _quiz.value = Resource.Error(
                        result.message ?: "Error al cargar quiz"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun enviarQuiz(quizId: String, respuestas: List<Int>) {
        viewModelScope.launch {
            _resultadoQuiz.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = educacionRepository.enviarQuiz(usuarioId, quizId, respuestas)) {
                is Resource.Success -> {
                    result.data?.let {
                        _resultadoQuiz.value = Resource.Success(it)
                    } ?: run {
                        _resultadoQuiz.value = Resource.Error("No se pudo procesar el resultado")
                    }
                    cargarProgreso()
                }
                is Resource.Error -> {
                    _resultadoQuiz.value = Resource.Error(
                        result.message ?: "Error al enviar quiz"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            when (val result = educacionRepository.obtenerCategorias()) {
                is Resource.Success -> {
                    _categorias.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _categorias.value = Resource.Error(
                        result.message ?: "Error al cargar categorías"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun filtrarPorCategoria(categoria: String?) {
        cargarContenidos(categoria = categoria, tipo = _filtroTipo.value)
    }

    fun filtrarPorTipo(tipo: String?) {
        cargarContenidos(categoria = _filtroCategoria.value, tipo = tipo)
    }

    fun limpiarFiltros() {
        cargarContenidos(categoria = null, tipo = null)
    }

    // ✅ CORREGIDO: Ahora puede asignar null
    fun limpiarResultadoQuiz() {
        _resultadoQuiz.value = null
    }

    fun limpiarQuiz() {
        _quiz.value = null
    }

    fun limpiarContenidoSeleccionado() {
        _contenidoSeleccionado.value = null
    }

    fun refresh() {
        cargarContenidos(_filtroCategoria.value, _filtroTipo.value)
        cargarProgreso()
    }
}