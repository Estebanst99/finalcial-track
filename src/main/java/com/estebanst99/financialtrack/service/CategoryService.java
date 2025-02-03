package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.Category;
import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.CategoryServiceException;
import com.estebanst99.financialtrack.repository.CategoryRepository;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Busca categorías por tipo y usuario.
     *
     * @param type      Tipo de categoría ('income' o 'expense').
     * @param userEmail Email del usuario.
     * @return Lista de categorías encontradas.
     * @throws CategoryServiceException Si ocurre algún error en la búsqueda.
     */
    public List<Category> findByTypeAndUser(String type, String userEmail) throws CategoryServiceException {
        try {
            return categoryRepository.findByTypeAndUserEmail(type, userEmail);
        } catch (Exception e) {
            throw new CategoryServiceException("Error al obtener categorías por tipo y usuario.", e);
        }
    }

    /**
     * Busca una categoría por nombre y usuario.
     *
     * @param name      Nombre de la categoría.
     * @param userEmail Email del usuario.
     * @return Categoría encontrada o vacío si no existe.
     * @throws CategoryServiceException Si ocurre un error durante la búsqueda.
     */
    public Optional<Category> findByNameAndUser(String name, String userEmail) throws CategoryServiceException {
        try {
            return categoryRepository.findByNameAndUserEmail(name, userEmail);
        } catch (Exception e) {
            throw new CategoryServiceException("Error al buscar la categoría por nombre y usuario.", e);
        }
    }

    /**
     * Busca una categoría por ID y usuario.
     *
     * @param id        ID de la categoría.
     * @param userEmail Email del usuario.
     * @return Categoría encontrada o vacío si no existe.
     * @throws CategoryServiceException Si ocurre un error durante la búsqueda.
     */
    public Optional<Category> findByIdAndUser(Long id, String userEmail) throws CategoryServiceException {
        try {
            return categoryRepository.findByIdAndUserEmail(id, userEmail);
        } catch (Exception e) {
            throw new CategoryServiceException("Error al buscar la categoría por ID y usuario.", e);
        }
    }

    /**
     * Obtiene todas las categorías asociadas a un usuario.
     *
     * @param userEmail Email del usuario.
     * @return Lista de categorías del usuario.
     * @throws CategoryServiceException Si ocurre un error durante la búsqueda.
     */
    public List<Category> findAllByUser(String userEmail) throws CategoryServiceException {
        try {
            return categoryRepository.findAllByUserEmail(userEmail);
        } catch (Exception e) {
            throw new CategoryServiceException("Error al obtener todas las categorías del usuario.", e);
        }
    }

    /**
     * Verifica si existe una categoría con el mismo nombre para el usuario.
     *
     * @param name      Nombre de la categoría.
     * @param userEmail Email del usuario.
     * @return True si la categoría existe, false en caso contrario.
     * @throws CategoryServiceException Si ocurre un error durante la verificación.
     */
    public boolean isCategoryDuplicate(String name, String userEmail) {
        return categoryRepository.findByNameAndUserEmail(name, userEmail).isPresent();
    }

    /**
     * Verifica si una categoría tiene dependencias, como transacciones o presupuestos asociados.
     *
     * @param categoryId ID de la categoría.
     * @return True si tiene dependencias, false en caso contrario.
     * @throws CategoryServiceException Si ocurre un error durante la verificación.
     */
    public boolean hasDependencies(Long categoryId) {
        // Aquí se puede implementar una lógica para verificar dependencias, como transacciones o presupuestos.
        return false; // Implementar lógica real según los requisitos
    }

    /**
     * Guarda o actualiza una categoría en la base de datos.
     *
     * @param category Categoría a guardar.
     * @return Categoría guardada.
     * @throws CategoryServiceException Si ocurre un error durante el guardado.
     */
    public Category save(Category category) throws CategoryServiceException {
        if (isCategoryDuplicate(category.getName(), category.getUser().getEmail())) {
            throw new CategoryServiceException("La categoría ya existe en el sistema.");
        }

        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new CategoryServiceException("Error al guardar la categoría.", e);
        }
    }

    /**
     * Elimina una categoría por su ID.
     *
     * @param id ID de la categoría a eliminar.
     * @throws CategoryServiceException Si ocurre un error durante la eliminación.
     */
    public void delete(Long id) throws CategoryServiceException {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryServiceException("La categoría con el ID proporcionado no existe.");
        }

        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new CategoryServiceException("Error al eliminar la categoría.", e);
        }
    }

    /**
     * Busca y devuelve un usuario por su email.
     *
     * @param email Email del usuario.
     * @return Usuario encontrado.
     * @throws CategoryServiceException Si no se encuentra el usuario o ocurre un error.
     */
    public User findUserByEmail(String email) throws CategoryServiceException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CategoryServiceException("Usuario no encontrado con email: " + email));
    }
}
