package com.ecocoins.campus.presentation.soporte

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.model.Ticket
import com.ecocoins.campus.data.repository.SoporteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoporteViewModel @Inject constructor(
    private val soporteRepository: SoporteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // ========== FAQs ==========

    private val _faqs = MutableLiveData<Resource<List<FAQ>>>()
    val faqs: LiveData<Resource<List<FAQ>>> = _faqs

    private val _faqSeleccionada = MutableLiveData<FAQ?>()
    val faqSeleccionada: LiveData<FAQ?> = _faqSeleccionada

    // ========== TICKETS ==========

    private val _tickets = MutableLiveData<Resource<List<Ticket>>>()
    val tickets: LiveData<Resource<List<Ticket>>> = _tickets

    private val _ticketSeleccionado = MutableLiveData<Resource<Ticket>>()
    val ticketSeleccionado: LiveData<Resource<Ticket>> = _ticketSeleccionado

    private val _crearTicketResult = MutableLiveData<Resource<Ticket>>()
    val crearTicketResult: LiveData<Resource<Ticket>> = _crearTicketResult

    private val _responderTicketResult = MutableLiveData<Resource<Ticket>>()
    val responderTicketResult: LiveData<Resource<Ticket>> = _responderTicketResult

    // ========== FILTROS ==========

    private val _filtroCategoria = MutableLiveData<String?>()
    val filtroCategoria: LiveData<String?> = _filtroCategoria

    private val _filtroEstado = MutableLiveData<String?>()
    val filtroEstado: LiveData<String?> = _filtroEstado

    init {
        cargarFAQs()
        cargarTickets()
    }

    // ========== FAQs METHODS ==========

    fun cargarFAQs(categoria: String? = null) {
        viewModelScope.launch {
            _faqs.value = Resource.Loading()
            _filtroCategoria.value = categoria

            when (val result = soporteRepository.obtenerFAQs(categoria)) {
                is Resource.Success -> {
                    _faqs.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _faqs.value = Resource.Error(
                        result.message ?: "Error al cargar FAQs"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun seleccionarFAQ(faq: FAQ) {
        _faqSeleccionada.value = faq
    }

    fun marcarFAQUtil(faqId: String) {
        viewModelScope.launch {
            when (soporteRepository.marcarFAQUtil(faqId)) {
                is Resource.Success -> {
                    cargarFAQs(_filtroCategoria.value)
                }
                is Resource.Error -> {
                    // Error silencioso
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun buscarFAQs(query: String): List<FAQ> {
        val todasLasFAQs = (_faqs.value as? Resource.Success)?.data ?: emptyList()

        if (query.isEmpty()) {
            return todasLasFAQs
        }

        return todasLasFAQs.filter {
            it.pregunta.contains(query, ignoreCase = true) ||
                    it.respuesta.contains(query, ignoreCase = true)
        }
    }

    // ========== TICKETS METHODS ==========

    fun cargarTickets(estado: String? = null) {
        viewModelScope.launch {
            _tickets.value = Resource.Loading()
            _filtroEstado.value = estado
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = soporteRepository.obtenerTickets(usuarioId, estado)) {
                is Resource.Success -> {
                    _tickets.value = Resource.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _tickets.value = Resource.Error(
                        result.message ?: "Error al cargar tickets"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarTicket(ticketId: String) {
        viewModelScope.launch {
            _ticketSeleccionado.value = Resource.Loading()

            when (val result = soporteRepository.obtenerTicket(ticketId)) {
                is Resource.Success -> {
                    _ticketSeleccionado.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _ticketSeleccionado.value = Resource.Error(
                        result.message ?: "Error al cargar ticket"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun crearTicket(
        asunto: String,
        descripcion: String,
        categoria: String,
        prioridad: String
    ) {
        viewModelScope.launch {
            _crearTicketResult.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = soporteRepository.crearTicket(
                usuarioId, asunto, descripcion, categoria, prioridad
            )) {
                is Resource.Success -> {
                    _crearTicketResult.value = Resource.Success(result.data!!)
                    cargarTickets(_filtroEstado.value)
                }
                is Resource.Error -> {
                    _crearTicketResult.value = Resource.Error(
                        result.message ?: "Error al crear ticket"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun responderTicket(ticketId: String, mensaje: String) {
        viewModelScope.launch {
            _responderTicketResult.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = soporteRepository.responderTicket(ticketId, usuarioId, mensaje)) {
                is Resource.Success -> {
                    _responderTicketResult.value = Resource.Success(result.data!!)
                    cargarTicket(ticketId)
                    cargarTickets(_filtroEstado.value)
                }
                is Resource.Error -> {
                    _responderTicketResult.value = Resource.Error(
                        result.message ?: "Error al responder ticket"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun filtrarPorCategoria(categoria: String?) {
        cargarFAQs(categoria)
    }

    fun filtrarPorEstado(estado: String?) {
        cargarTickets(estado)
    }

    fun limpiarCrearTicketResult() {
        _crearTicketResult.value = null
    }

    fun limpiarFiltros() {
        _filtroCategoria.value = null
        _filtroEstado.value = null
        cargarFAQs()
        cargarTickets()
    }

    fun refresh() {
        cargarFAQs(_filtroCategoria.value)
        cargarTickets(_filtroEstado.value)
    }
}