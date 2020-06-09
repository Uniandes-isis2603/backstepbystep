/*
MIT License

Copyright (c) 2017 Universidad de los Andes - ISIS2603

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.bookstore.ejb;

import co.edu.uniandes.csw.bookstore.entities.AuthorEntity;
import co.edu.uniandes.csw.bookstore.entities.BookEntity;
import co.edu.uniandes.csw.bookstore.entities.PrizeEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.persistence.AuthorPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Clase que implementa la conexion con la persistencia para la entidad de
 * Author.
 *
 * @author ISIS2603
 */
@Stateless
public class AuthorLogic {

    private static final Logger LOGGER = Logger.getLogger(AuthorLogic.class.getName());

    @Inject
    private AuthorPersistence persistence;

    /**
     * Se encarga de crear un Author en la base de datos.
     *
     * @param authorEntity Objeto de AuthorEntity con los datos nuevos
     * @return Objeto de AuthorEntity con los datos nuevos y su ID.
     */
    public AuthorEntity createAuthor(AuthorEntity authorEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de creación del autor");
        if (authorEntity.getName().length() <= 2) {
            throw new BusinessLogicException("El nombre del autor es muy corto");
        }
        if (authorEntity.getDescription().length() <= 2) {
            throw new BusinessLogicException("La descripción del autor es muy corta");
        }
        AuthorEntity newAuthorEntity = persistence.create(authorEntity);
        LOGGER.log(Level.INFO, "Termina proceso de creación del autor");
        return newAuthorEntity;
    }

    /**
     * Obtiene la lista de los registros de Author.
     *
     * @return Colección de objetos de AuthorEntity.
     */
    public List<AuthorEntity> getAuthors() {
        LOGGER.log(Level.INFO, "Inicia proceso de consultar todos los autores");
        List<AuthorEntity> lista = persistence.findAll();
        LOGGER.log(Level.INFO, "Termina proceso de consultar todos los autores");
        return lista;
    }

    /**
     * Obtiene los datos de una instancia de Author a partir de su ID.
     *
     * @param authorsId Identificador de la instancia a consultar
     * @return Instancia de AuthorEntity con los datos del Author consultado.
     */
    public AuthorEntity getAuthor(Long authorsId) {
        LOGGER.log(Level.INFO, "Inicia proceso de consultar el autor con id = {0}", authorsId);
        AuthorEntity authorEntity = persistence.find(authorsId);
        if (authorEntity == null) {
            LOGGER.log(Level.SEVERE, "La editorial con el id = {0} no existe", authorsId);
        }
        LOGGER.log(Level.INFO, "Termina proceso de consultar el autor con id = {0}", authorsId);
        return authorEntity;
    }

    /**
     * Actualiza la información de una instancia de Author.
     *
     * @param authorsId Identificador de la instancia a actualizar
     * @param authorEntity Instancia de AuthorEntity con los nuevos datos.
     * @return Instancia de AuthorEntity con los datos actualizados.
     */
    public AuthorEntity updateAuthor(Long authorsId, AuthorEntity authorEntity) {
        LOGGER.log(Level.INFO, "Inicia proceso de actualizar el autor con id = {0}", authorsId);
        AuthorEntity newAuthorEntity = persistence.update(authorEntity);
        LOGGER.log(Level.INFO, "Termina proceso de actualizar el autor con id = {0}", authorsId);
        return newAuthorEntity;
    }

    /**
     * Elimina una instancia de Author de la base de datos.
     *
     * @param authorsId Identificador de la instancia a eliminar.
     * @throws BusinessLogicException si el autor tiene libros asociados.
     */
    public void deleteAuthor(Long authorsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de borrar el autor con id = {0}", authorsId);
        List<BookEntity> books = getAuthor(authorsId).getBooks();
        if (books != null && !books.isEmpty()) {
            throw new BusinessLogicException("No se puede borrar el autor con id = " + authorsId + " porque tiene books asociados");
        }
        List<PrizeEntity> prizes = getAuthor(authorsId).getPrizes();
        if (prizes != null && !prizes.isEmpty()) {
            throw new BusinessLogicException("No se puede borrar el autor con id = " + authorsId + " porque tiene premios asociados");
        }
        persistence.delete(authorsId);
        LOGGER.log(Level.INFO, "Termina proceso de borrar el autor con id = {0}", authorsId);
    }
}
