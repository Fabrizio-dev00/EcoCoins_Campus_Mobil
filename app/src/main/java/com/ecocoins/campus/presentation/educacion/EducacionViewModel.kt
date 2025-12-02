package com.ecocoins.campus.presentation.educacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.CategoriaEducativa
import com.ecocoins.campus.data.model.ContenidoEducativo
import com.ecocoins.campus.data.model.ProgresoEducativo
import com.ecocoins.campus.data.model.Quiz
import com.ecocoins.campus.data.model.ResultadoQuiz
import com.ecocoins.campus.data.repository.EducacionRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EducacionViewModel @Inject constructor(
    private val educacionRepository: EducacionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _contenidos = MutableLiveData<List<ContenidoEducativo>>()
    val contenidos: LiveData<List<ContenidoEducativo>> = _contenidos

    private val _selectedContenido = MutableLiveData<ContenidoEducativo?>()
    val selectedContenido: LiveData<ContenidoEducativo?> = _selectedContenido

    private val _quizzes = MutableLiveData<List<Quiz>>()
    val quizzes: LiveData<List<Quiz>> = _quizzes

    private val _selectedQuiz = MutableLiveData<Quiz?>()
    val selectedQuiz: LiveData<Quiz?> = _selectedQuiz

    private val _resultadoQuiz = MutableLiveData<Resource<ResultadoQuiz>>()
    val resultadoQuiz: LiveData<Resource<ResultadoQuiz>> = _resultadoQuiz

    private val _categorias = MutableLiveData<List<CategoriaEducativa>>()
    val categorias: LiveData<List<CategoriaEducativa>> = _categorias

    private val _progreso = MutableLiveData<ProgresoEducativo?>()
    val progreso: LiveData<ProgresoEducativo?> = _progreso

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadContenidos()
        loadQuizzes()
        loadCategorias()
        loadProgreso()
    }

    fun loadContenidos(categoria: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            val flow = if (categoria != null) {
                educacionRepository.getContenidosPorCategoria(categoria)
            } else {
                educacionRepository.getContenidosEducativos()
            }

            flow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _contenidos.value = resource.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun getContenidoById(contenidoId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            educacionRepository.getContenidoById(contenidoId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _selectedContenido.value = resource.data
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun completarContenido(contenidoId: Long) {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                educacionRepository.completarContenido(contenidoId, userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            loadProgreso() // Actualizar progreso
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            educacionRepository.getQuizzes().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _quizzes.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun getQuizById(quizId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            educacionRepository.getQuizById(quizId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _selectedQuiz.value = resource.data
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }

    fun completarQuiz(quizId: Long, respuestas: Map<Long, Int>) {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                educacionRepository.completarQuiz(quizId, userId, respuestas).collect { resource ->
                    _resultadoQuiz.value = resource

                    if (resource is Resource.Success) {
                        loadProgreso() // Actualizar progreso
                    }
                }
            } else {
                _error.value = "Usuario no autenticado"
            }
        }
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            educacionRepository.getCategoriasEducativas().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _categorias.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun loadProgreso() {
        viewModelScope.launch {
            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                educacionRepository.getProgresoEducativo(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _progreso.value = resource.data
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    fun resetResultadoQuiz() {
        _resultadoQuiz.value = null
    }

    fun clearError() {
        _error.value = null
    }
}