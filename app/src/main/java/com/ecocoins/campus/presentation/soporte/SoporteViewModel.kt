package com.ecocoins.campus.presentation.soporte

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.data.model.Ticket
import com.ecocoins.campus.data.repository.SoporteRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoporteViewModel @Inject constructor(
    private val soporteRepository: SoporteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _faqs = MutableLiveData<List<FAQ>>()
    val faqs: LiveData<List<FAQ>> = _faqs

    private val _tickets = MutableLiveData<List<Ticket>>()
    val tickets: LiveData<List<Ticket>> = _tickets

    private val _selectedTicket = MutableLiveData<Ticket?>()
    val selectedTicket: LiveData<Ticket?> = _selectedTicket

    private val _crearTicketState = MutableLiveData<Resource<Ticket>>()
    val crearTicketState: LiveData<Resource<Ticket>> = _crearTicketState

    private val _responderTicketState = MutableLiveData<Resource<String>>()
    val responderTicketState: LiveData<Resource<String>> = _responderTicketState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadFAQs()
        loadTickets()
    }

    fun loadFAQs(categoria: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            val flow = if (categoria != null) {
                soporteRepository.getFAQsPorCategoria(categoria)
            } else {
                soporteRepository.getFAQs()
            }

            flow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _faqs.value = resource.data ?: emptyList()
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

    fun marcarFAQUtil(faqId: String) {
        viewModelScope.launch {
            soporteRepository.marcarFAQUtil(faqId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadFAQs() // Recargar FAQs
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun loadTickets() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                soporteRepository.getTicketsUsuario(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _tickets.value = resource.data ?: emptyList()
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
            } else {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
            }
        }
    }

    fun getTicketById(ticketId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            soporteRepository.getTicketById(ticketId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _selectedTicket.value = resource.data
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

    fun crearTicket(ticket: Ticket) {
        viewModelScope.launch {
            soporteRepository.crearTicket(ticket).collect { resource ->
                _crearTicketState.value = resource

                if (resource is Resource.Success) {
                    loadTickets() // Recargar tickets
                }
            }
        }
    }

    fun responderTicket(ticketId: Long, respuesta: String) {
        viewModelScope.launch {
            soporteRepository.responderTicket(ticketId, respuesta).collect { resource ->
                _responderTicketState.value = resource

                if (resource is Resource.Success) {
                    getTicketById(ticketId) // Recargar ticket
                }
            }
        }
    }

    fun cerrarTicket(ticketId: Long) {
        viewModelScope.launch {
            soporteRepository.cerrarTicket(ticketId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadTickets() // Recargar tickets
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun resetCrearTicketState() {
        _crearTicketState.value = null
    }

    fun resetResponderTicketState() {
        _responderTicketState.value = null
    }

    fun clearError() {
        _error.value = null
    }
}