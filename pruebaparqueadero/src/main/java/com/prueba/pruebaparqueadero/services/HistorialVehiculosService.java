package com.prueba.pruebaparqueadero.services;

import com.prueba.pruebaparqueadero.entities.HistorialVehiculos;
import com.prueba.pruebaparqueadero.entities.Rol;
import com.prueba.pruebaparqueadero.entities.Usuario;
import com.prueba.pruebaparqueadero.entities.Vehiculo;
import com.prueba.pruebaparqueadero.exceptions.ConflictException;
import com.prueba.pruebaparqueadero.exceptions.NotFoundException;
import com.prueba.pruebaparqueadero.repositories.HistorialVehiculosRepository;
import com.prueba.pruebaparqueadero.services.dtos.res.RegistroResponseDTO;
import com.prueba.pruebaparqueadero.services.dtos.res.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistorialVehiculosService {

    private final HistorialVehiculosRepository historialVehiculosRepository;
    private final ModelMapper modelMapper;

    public HistorialVehiculos save( HistorialVehiculos historial) {
        return historialVehiculosRepository.save(historial);
    }

    public Optional<HistorialVehiculos> findByVehiculoAndSalidaIsNull(Vehiculo vehiculo){
        return historialVehiculosRepository.findByVehiculoAndSalidaIsNull(vehiculo);

    }

    public Page<HistorialVehiculos> findByPorParqueadero(int idParqueadero, Pageable pageable){
        return historialVehiculosRepository.findByPorParqueadero(idParqueadero, pageable);
    }

    public List<HistorialVehiculos> findByEntradaBetween(int idParqueadero, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return historialVehiculosRepository.findByEntradaBetween(idParqueadero, fechaInicio, fechaFin);
    }

    public List<Object[]> findVehiculosMasRegistrados(){
        return historialVehiculosRepository.findVehiculosMasRegistrados();
    }

    public List<Object[]> findVehiculosMasRegistradosPorParqueadero(int idParqueadero){
        return historialVehiculosRepository.findVehiculosMasRegistradosPorParqueadero(idParqueadero);
    }

    public long countEntriesByPlacaAndParqueadero(String placa, int idParqueadero){
        return historialVehiculosRepository.countEntriesByPlacaAndParqueadero(placa, idParqueadero);
    }

}