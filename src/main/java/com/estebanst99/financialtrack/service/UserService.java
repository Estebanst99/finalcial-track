package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.UserServiceException;
import com.estebanst99.financialtrack.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    //TODO añadir el logger


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario.
     * @return Usuario encontrado o vacío si no existe.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param user Usuario a guardar.
     * @return Usuario creado.
     */
    @Transactional
    public User save(User user) throws UserServiceException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserServiceException("El email ya está registrado.");
        }
        return userRepository.save(user);
    }

}
