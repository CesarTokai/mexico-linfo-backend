# Consumo de APIs desde Frontend — Vue 3 + JavaScript

## Configuración Base

### `services/api.js`

```javascript
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

function getHeaders() {
  const session = JSON.parse(localStorage.getItem('mlt_session') || '{}')
  return {
    'Content-Type': 'application/json',
    ...(session.token && { 'Authorization': `Bearer ${session.token}` })
  }
}

async function apiCall(endpoint, method = 'GET', body = null) {
  const opts = { method, headers: getHeaders() }
  if (body) opts.body = JSON.stringify(body)

  try {
    const res = await fetch(`${API_URL}${endpoint}`, opts)
    
    if (res.status === 401) {
      localStorage.removeItem('mlt_session')
      window.location.href = '/login'
      return
    }
    
    if (!res.ok) {
      const err = await res.json()
      throw new Error(err.message || `HTTP ${res.status}`)
    }
    
    return await res.json()
  } catch (error) {
    console.error(`API Error [${method} ${endpoint}]:`, error.message)
    throw error
  }
}

// ============================================
// AUTH
// ============================================
export const postLogin = (correo, password) => 
  apiCall('/auth/login', 'POST', { correo, password })

// ============================================
// USUARIOS
// ============================================
export const getUsuarios = () => apiCall('/usuarios')
export const postUsuario = (data) => apiCall('/usuarios', 'POST', data)
export const putUsuario = (id, data) => apiCall(`/usuarios/${id}`, 'PUT', data)
export const deleteUsuario = (id) => apiCall(`/usuarios/${id}`, 'DELETE')

// ============================================
// CAMIONETAS
// ============================================
export const getCamionetas = () => apiCall('/camionetas')
export const postCamioneta = (data) => apiCall('/camionetas', 'POST', data)
export const putCamioneta = (id, data) => apiCall(`/camionetas/${id}`, 'PUT', data)
export const deleteCamioneta = (id) => apiCall(`/camionetas/${id}`, 'DELETE')
export const getCamionetaHistorial = (id) => apiCall(`/camionetas/${id}/historial`)

// ============================================
// CHOFERES
// ============================================
export const getChoferes = () => apiCall('/choferes')
export const getChoferesTodos = () => apiCall('/choferes/todos')
export const postChofer = (data) => apiCall('/choferes', 'POST', data)
export const putChofer = (id, data) => apiCall(`/choferes/${id}`, 'PUT', data)
export const deleteChofer = (id) => apiCall(`/choferes/${id}`, 'DELETE')
export const getChoferHistorial = (id) => apiCall(`/choferes/${id}/historial`)
export const getChoferDisponibilidad = (id) => apiCall(`/choferes/${id}/disponibilidad`)
export const postChoferDisponibilidad = (id, data) => apiCall(`/choferes/${id}/disponibilidad`, 'POST', data)
export const deleteChoferDisponibilidad = (disponibilidadId) => 
  apiCall(`/choferes/disponibilidad/${disponibilidadId}`, 'DELETE')

// ============================================
// CLIENTES
// ============================================
export const getClientes = () => apiCall('/clientes')
export const postCliente = (data) => apiCall('/clientes', 'POST', data)
export const putCliente = (id, data) => apiCall(`/clientes/${id}`, 'PUT', data)
export const deleteCliente = (id) => apiCall(`/clientes/${id}`, 'DELETE')
export const getClienteHistorial = (id) => apiCall(`/clientes/${id}/historial`)

// ============================================
// VIAJES
// ============================================
export const getViajes = (params = {}) => {
  const qs = new URLSearchParams(params).toString()
  return apiCall(`/viajes${qs ? '?' + qs : ''}`)
}
export const getViajesCamioneta = (camionetaId) => 
  apiCall(`/viajes/camioneta/${camionetaId}`)
export const getVijesCliente = (clienteId) => 
  apiCall(`/viajes/cliente/${clienteId}`)
export const postViaje = (data) => apiCall('/viajes', 'POST', data)
export const putViaje = (id, data) => apiCall(`/viajes/${id}`, 'PUT', data)
export const putViajeFinalizarViaje = (id, data) => 
  apiCall(`/viajes/${id}/finalizar`, 'PUT', data)
export const putViajeEstado = (id, data) => apiCall(`/viajes/${id}/estado`, 'PUT', data)
export const deleteViajeCancelar = (id) => 
  apiCall(`/viajes/${id}/cancelar`, 'DELETE')

// ============================================
// VIAJES - PAGOS
// ============================================
export const getViajePagos = (id) => apiCall(`/viajes/${id}/pagos`)
export const postViajePago = (id, data) => apiCall(`/viajes/${id}/pagos`, 'POST', data)
export const putViajePago = (viaje_id, pago_id, data) => 
  apiCall(`/viajes/${viaje_id}/pagos/${pago_id}`, 'PUT', data)
export const deleteViajePago = (pagoId) => 
  apiCall(`/viajes/pagos/${pagoId}`, 'DELETE')

// ============================================
// VIAJES - GASTOS
// ============================================
export const getViajeGastos = (id) => apiCall(`/viajes/${id}/gastos`)
export const postViajeGasto = (id, data) => apiCall(`/viajes/${id}/gastos`, 'POST', data)
export const putViajeGasto = (viaje_id, gasto_id, data) => 
  apiCall(`/viajes/${viaje_id}/gastos/${gasto_id}`, 'PUT', data)
export const deleteViajeGasto = (gastoId) => 
  apiCall(`/viajes/gastos/${gastoId}`, 'DELETE')

// ============================================
// MANTENIMIENTOS
// ============================================
export const getMantenimientos = () => apiCall('/mantenimientos')
export const getMantenimientosPorCamioneta = (camionetaId) => 
  apiCall(`/mantenimientos/camioneta/${camionetaId}`)
export const postMantenimiento = (data) => apiCall('/mantenimientos', 'POST', data)
export const putMantenimiento = (id, data) => apiCall(`/mantenimientos/${id}`, 'PUT', data)
export const deleteMantenimiento = (id) => apiCall(`/mantenimientos/${id}`, 'DELETE')

// ============================================
// TRAMITES
// ============================================
export const getTramites = () => apiCall('/tramites')
export const getTramitesPorCamioneta = (camionetaId) => 
  apiCall(`/tramites/camioneta/${camionetaId}`)
export const postTramite = (data) => apiCall('/tramites', 'POST', data)
export const putTramite = (id, data) => apiCall(`/tramites/${id}`, 'PUT', data)
export const deleteTramite = (id) => apiCall(`/tramites/${id}`, 'DELETE')

// ============================================
// GASTOS GENERALES
// ============================================
export const getGastosGenerales = (params = {}) => {
  const qs = new URLSearchParams(params).toString()
  return apiCall(`/gastos-generales${qs ? '?' + qs : ''}`)
}
export const postGastoGeneral = (data) => apiCall('/gastos-generales', 'POST', data)
export const putGastoGeneral = (id, data) => apiCall(`/gastos-generales/${id}`, 'PUT', data)
export const deleteGastoGeneral = (id) => apiCall(`/gastos-generales/${id}`, 'DELETE')

// ============================================
// CALENDARIO
// ============================================
export const getCalendario = (desde, hasta) => {
  const qs = new URLSearchParams({ desde, hasta }).toString()
  return apiCall(`/calendario?${qs}`)
}

// ============================================
// AVISOS
// ============================================
export const getAvisos = () => apiCall('/avisos')
export const getAvisosMantenimientos = () => apiCall('/avisos/mantenimientos')
export const getAvisosTramites = () => apiCall('/avisos/tramites')

// ============================================
// DASHBOARD
// ============================================
export const getDashboardMes = (mes, anio) => 
  apiCall(`/dashboard/mes?mes=${mes}&anio=${anio}`)
export const getDashboardAnio = (anio) => 
  apiCall(`/dashboard/anio?anio=${anio}`)
export const getDashboardAcumulado = () => 
  apiCall('/dashboard/acumulado')

// ============================================
// TOTALES
// ============================================
export const getTotalesMes = (mes, anio) => 
  apiCall(`/totales?mes=${mes}&anio=${anio}`)
export const getTotalesAnio = (anio) => 
  apiCall(`/totales?anio=${anio}`)
export const getTotalesAcumulado = () => 
  apiCall('/totales')
```

---

## Uso en Componentes Vue 3

### Login

```vue
<template>
  <div class="login">
    <h2>Iniciar Sesión</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="form.correo" type="email" placeholder="Correo" required />
      <input v-model="form.password" type="password" placeholder="Contraseña" required />
      <button type="submit" :disabled="loading">{{ loading ? 'Iniciando...' : 'Entrar' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import * as api from '@/services/api'

const router = useRouter()
const loading = ref(false)
const error = ref(null)
const form = ref({
  correo: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.postLogin(form.value.correo, form.value.password)
    localStorage.setItem('mlt_session', JSON.stringify({
      token: res.token,
      usuario: {
        nombre: res.nombre,
        correo: res.correo,
        rol: res.rol
      }
    }))
    router.push('/dashboard')
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>
```

---

### CRUD Camionetas

```vue
<template>
  <div class="camionetas">
    <h2>Camionetas</h2>
    
    <button @click="showFormCrear = true">+ Nueva Camioneta</button>
    
    <div v-if="loading" class="loading">Cargando...</div>
    <div v-if="error" class="error">{{ error }}</div>
    
    <table v-if="camionetas.length">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Modelo</th>
          <th>Capacidad</th>
          <th>KM Actual</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in camionetas" :key="c.id">
          <td>{{ c.nombre }}</td>
          <td>{{ c.modelo }}</td>
          <td>{{ c.capacidad }}</td>
          <td>{{ c.kmActual }}</td>
          <td>{{ c.estado }}</td>
          <td>
            <button @click="editar(c)">Editar</button>
            <button @click="eliminar(c.id)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div v-if="showFormCrear" class="modal">
      <div class="modal-content">
        <h3>{{ editandoId ? 'Editar' : 'Crear' }} Camioneta</h3>
        <form @submit.prevent="guardar">
          <input v-model="formulario.nombre" placeholder="Nombre" required />
          <input v-model="formulario.modelo" placeholder="Modelo" required />
          <input v-model.number="formulario.capacidad" type="number" placeholder="Capacidad" required />
          <button type="submit" :disabled="guardando">{{ guardando ? 'Guardando...' : 'Guardar' }}</button>
          <button type="button" @click="cancelar">Cancelar</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as api from '@/services/api'

const camionetas = ref([])
const loading = ref(false)
const error = ref(null)
const guardando = ref(false)
const showFormCrear = ref(false)
const editandoId = ref(null)
const formulario = ref({
  nombre: '',
  modelo: '',
  capacidad: 14
})

const cargar = async () => {
  loading.value = true
  error.value = null
  try {
    camionetas.value = await api.getCamionetas()
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

const guardar = async () => {
  guardando.value = true
  try {
    if (editandoId.value) {
      await api.putCamioneta(editandoId.value, formulario.value)
    } else {
      await api.postCamioneta(formulario.value)
    }
    await cargar()
    cancelar()
  } catch (err) {
    error.value = err.message
  } finally {
    guardando.value = false
  }
}

const editar = (camioneta) => {
  editandoId.value = camioneta.id
  formulario.value = { ...camioneta }
  showFormCrear.value = true
}

const eliminar = async (id) => {
  if (!confirm('¿Eliminar camioneta?')) return
  try {
    await api.deleteCamioneta(id)
    await cargar()
  } catch (err) {
    error.value = err.message
  }
}

const cancelar = () => {
  showFormCrear.value = false
  editandoId.value = null
  formulario.value = { nombre: '', modelo: '', capacidad: 14 }
}

onMounted(() => cargar())
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  min-width: 400px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background: #0056b3;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

thead {
  background: #f5f5f5;
}

th, td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #ddd;
}
</style>
```

---

### Crear Viaje

```vue
<template>
  <div class="crear-viaje">
    <h2>Crear Viaje</h2>
    
    <form @submit.prevent="guardar">
      <div class="form-group">
        <label>Cliente</label>
        <select v-model="form.clienteId" required>
          <option value="">-- Seleccionar --</option>
          <option v-for="c in clientes" :key="c.id" :value="c.id">
            {{ c.nombre }}
          </option>
        </select>
      </div>
      
      <div class="form-group">
        <label>Camioneta</label>
        <select v-model="form.camionetaId" required>
          <option value="">-- Seleccionar --</option>
          <option v-for="cm in camionetas" :key="cm.id" :value="cm.id">
            {{ cm.nombre }} ({{ cm.estado }})
          </option>
        </select>
      </div>
      
      <div class="form-group">
        <label>Chofer</label>
        <select v-model="form.choferId">
          <option value="">-- Opcional --</option>
          <option v-for="ch in choferes" :key="ch.id" :value="ch.id">
            {{ ch.nombre }}
          </option>
        </select>
      </div>
      
      <div class="form-group">
        <label>Concepto</label>
        <input v-model="form.concepto" type="text" placeholder="Ej: Viaje a Cancún" required />
      </div>
      
      <div class="form-row">
        <div class="form-group">
          <label>Fecha Inicio</label>
          <input v-model="form.fechaInicio" type="date" required />
        </div>
        <div class="form-group">
          <label>Fecha Fin</label>
          <input v-model="form.fechaFin" type="date" required />
        </div>
      </div>
      
      <div class="form-group">
        <label>Costo Total</label>
        <input v-model.number="form.costoTotal" type="number" step="0.01" required />
      </div>
      
      <div class="form-group">
        <label>Notas</label>
        <textarea v-model="form.notas" placeholder="Notas adicionales..."></textarea>
      </div>
      
      <div class="form-actions">
        <button type="submit" :disabled="guardando">
          {{ guardando ? 'Guardando...' : 'Crear Viaje' }}
        </button>
        <button type="button" @click="$router.back()">Cancelar</button>
      </div>
      
      <div v-if="error" class="error">{{ error }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as api from '@/services/api'

const router = useRouter()
const clientes = ref([])
const camionetas = ref([])
const choferes = ref([])
const guardando = ref(false)
const error = ref(null)

const form = ref({
  clienteId: '',
  camionetaId: '',
  choferId: '',
  concepto: '',
  fechaInicio: '',
  fechaFin: '',
  costoTotal: '',
  notas: ''
})

const guardar = async () => {
  guardando.value = true
  error.value = null
  try {
    const data = {
      clienteId: parseInt(form.value.clienteId),
      camionetaId: parseInt(form.value.camionetaId),
      choferId: form.value.choferId ? parseInt(form.value.choferId) : null,
      concepto: form.value.concepto,
      fechaInicio: form.value.fechaInicio,
      fechaFin: form.value.fechaFin,
      costoTotal: parseFloat(form.value.costoTotal),
      notas: form.value.notas
    }
    await api.postViaje(data)
    router.push('/viajes')
  } catch (err) {
    error.value = err.message
  } finally {
    guardando.value = false
  }
}

onMounted(async () => {
  clientes.value = await api.getClientes()
  camionetas.value = await api.getCamionetas()
  choferes.value = await api.getChoferes()
})
</script>

<style scoped>
.crear-viaje {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

label {
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

input, select, textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

button {
  padding: 0.75rem 1.5rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

button:hover {
  background: #0056b3;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error {
  padding: 1rem;
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}
</style>
```

---

### Dashboard

```vue
<template>
  <div class="dashboard">
    <h2>Dashboard Ganancias</h2>
    
    <div class="filtros">
      <select v-model="periodo">
        <option value="mes">Mes Actual</option>
        <option value="anio">Año Actual</option>
        <option value="acumulado">Acumulado</option>
      </select>
      
      <template v-if="periodo === 'mes'">
        <input v-model.number="mes" type="number" min="1" max="12" @change="cargar" />
        <input v-model.number="anio" type="number" @change="cargar" />
      </template>
      
      <template v-if="periodo === 'anio'">
        <input v-model.number="anio" type="number" @change="cargar" />
      </template>
      
      <button @click="cargar">Cargar</button>
    </div>
    
    <div v-if="loading" class="loading">Cargando...</div>
    <div v-if="error" class="error">{{ error }}</div>
    
    <div v-if="dashboard" class="resumen">
      <div class="card">
        <h3>Ingresos</h3>
        <p class="valor">{{ formatMoney(dashboard.ingresosTotal) }}</p>
      </div>
      <div class="card">
        <h3>Egresos</h3>
        <p class="valor">{{ formatMoney(dashboard.egresosTotal) }}</p>
      </div>
      <div class="card neto">
        <h3>Neto</h3>
        <p class="valor">{{ formatMoney(dashboard.netoTotal) }}</p>
      </div>
    </div>
    
    <table v-if="dashboard && dashboard.camionetas.length">
      <thead>
        <tr>
          <th>Camioneta</th>
          <th>Ingresos</th>
          <th>Egresos</th>
          <th>Neto</th>
          <th>Viajes</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in dashboard.camionetas" :key="c.camionetaId">
          <td>{{ c.camionetaNombre }}</td>
          <td>{{ formatMoney(c.ingresos) }}</td>
          <td>{{ formatMoney(c.egresos) }}</td>
          <td class="positivo">{{ formatMoney(c.neto) }}</td>
          <td>{{ c.viajesCompletados }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import * as api from '@/services/api'

const periodo = ref('mes')
const mes = ref(new Date().getMonth() + 1)
const anio = ref(new Date().getFullYear())
const dashboard = ref(null)
const loading = ref(false)
const error = ref(null)

const formatMoney = (value) => {
  return new Intl.NumberFormat('es-MX', {
    style: 'currency',
    currency: 'MXN'
  }).format(value)
}

const cargar = async () => {
  loading.value = true
  error.value = null
  try {
    if (periodo.value === 'mes') {
      dashboard.value = await api.getDashboardMes(mes.value, anio.value)
    } else if (periodo.value === 'anio') {
      dashboard.value = await api.getDashboardAnio(anio.value)
    } else {
      dashboard.value = await api.getDashboardAcumulado()
    }
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

watch(periodo, cargar)

// Cargar al montar
cargar()
</script>

<style scoped>
.dashboard {
  padding: 2rem;
}

.filtros {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
}

.filtros select,
.filtros input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.filtros button {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.resumen {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.card {
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.card h3 {
  margin-top: 0;
  color: #666;
  font-size: 0.9rem;
  text-transform: uppercase;
}

.card .valor {
  font-size: 1.8rem;
  font-weight: bold;
  color: #007bff;
  margin: 0;
}

.card.neto .valor {
  color: #28a745;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f5f5f5;
}

th, td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.positivo {
  color: #28a745;
  font-weight: bold;
}

.error {
  padding: 1rem;
  background: #f8d7da;
  color: #721c24;
  border-radius: 4px;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}
</style>
```

---

## Variables de Entorno

### `.env.local`

```
VITE_API_URL=http://localhost:8080/api
```

### `.env.production`

```
VITE_API_URL=https://api.mexicolindo.com/api
```

---

## Manejo de Errores Común

```javascript
// En cualquier componente
try {
  const datos = await api.getViajes()
  // procesar datos
} catch (error) {
  if (error.message.includes('401')) {
    // Redirigir a login
  } else if (error.message.includes('404')) {
    // No encontrado
  } else if (error.message.includes('500')) {
    // Error del servidor
  }
  console.error('Error:', error.message)
}
```

---

## Tips

1. **Token automático:** Se obtiene de `localStorage.getItem('mlt_session')`
2. **Logout:** Elimina token y redirige a `/login`
3. **Dinero:** Usa `parseFloat()` antes de enviar, `formatMoney()` al mostrar
4. **Fechas:** Formato `YYYY-MM-DD` en inputs y requests
5. **Validación:** Siempre validar en cliente ANTES de enviar al servidor
6. **Loading:** Mostrar estado mientras se carga datos
7. **Errores:** Capturar y mostrar al usuario en lugar de fallar silenciosamente
