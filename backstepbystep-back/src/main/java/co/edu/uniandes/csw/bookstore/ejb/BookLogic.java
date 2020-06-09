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
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.persistence.AuthorPersistence;
import co.edu.uniandes.csw.bookstore.persistence.BookPersistence;
import co.edu.uniandes.csw.bookstore.persistence.EditorialPersistence;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Clase que implementa la conexion con la persistencia para la entidad de Book.
 *
 * @author ISIS2603
 */
@Stateless
public class BookLogic {

    private static final Logger LOGGER = Logger.getLogger(BookLogic.class.getName());

    @Inject
    private BookPersistence persistence;

    @Inject
    private EditorialPersistence editorialPersistence;

    @Inject
    private AuthorPersistence authorPersistence;

    /**
     * Guardar un nuevo libro
     *
     * @param bookEntity La entidad de tipo libro del nuevo libro a persistir.
     * @return La entidad luego de persistirla
     * @throws BusinessLogicException Si el ISBN es inválido o ya existe en la
     * persistencia.
     */
    public BookEntity createBook(BookEntity bookEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de creación del libro");
        if (bookEntity.getEditorial() == null || editorialPersistence.find(bookEntity.getEditorial().getId()) == null) {
            throw new BusinessLogicException("La editorial es inválida");
        }
        if (bookEntity.getAuthors() == null) {
            throw new BusinessLogicException("Autor(es) inválido(s)");
        }
        for (AuthorEntity author: bookEntity.getAuthors()) {
            if (author == null || authorPersistence.find(author.getId()) == null) {
                throw new BusinessLogicException("Autor(es) inválido(s)");
            }
        }
        if (!validateISBN(bookEntity.getIsbn())) {
            throw new BusinessLogicException("El ISBN es inválido");
        }
        if (persistence.findByISBN(bookEntity.getIsbn()) != null) {
            throw new BusinessLogicException("El ISBN ya existe");
        }
        if (bookEntity.getName().length() <= 8) {
            throw new BusinessLogicException("El nombre del libro es muy corto");
        }
        if (bookEntity.getDescription().length() <= 8) {
            throw new BusinessLogicException("La descripción del libro es muy corta");
        }
        if (bookEntity.getImage().isEmpty()) {
            throw new BusinessLogicException("No hay ninguna imagen");
        }
        persistence.create(bookEntity);
        LOGGER.log(Level.INFO, "Termina proceso de creación del libro");
        return bookEntity;
    }

    /**
     * Devuelve todos los libros que hay en la base de datos.
     *
     * @return Lista de entidades de tipo libro.
     */
    public List<BookEntity> getBooks() {
        LOGGER.log(Level.INFO, "Inicia proceso de consultar todos los libros");
        List<BookEntity> books = persistence.findAll();
        LOGGER.log(Level.INFO, "Termina proceso de consultar todos los libros");
        return books;
    }

    /**
     * Busca un libro por ID
     *
     * @param booksId El id del libro a buscar
     * @return El libro encontrado, null si no lo encuentra.
     */
    public BookEntity getBook(Long booksId) {
        LOGGER.log(Level.INFO, "Inicia proceso de consultar el libro con id = {0}", booksId);
        BookEntity bookEntity = persistence.find(booksId);
        if (bookEntity == null) {
            LOGGER.log(Level.SEVERE, "El libro con el id = {0} no existe", booksId);
        }
        LOGGER.log(Level.INFO, "Termina proceso de consultar el libro con id = {0}", booksId);
        return bookEntity;
    }

    /**
     * Actualizar un libro por ID
     *
     * @param booksId El ID del libro a actualizar
     * @param bookEntity La entidad del libro con los cambios deseados
     * @return La entidad del libro luego de actualizarla
     * @throws BusinessLogicException Si el IBN de la actualización es inválido
     */
    public BookEntity updateBook(Long booksId, BookEntity bookEntity) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de actualizar el libro con id = {0}", booksId);
        if (!validateISBN(bookEntity.getIsbn())) {
            throw new BusinessLogicException("El ISBN es inválido");
        }
        BookEntity newEntity = persistence.update(bookEntity);
        LOGGER.log(Level.INFO, "Termina proceso de actualizar el libro con id = {0}", bookEntity.getId());
        return newEntity;
    }

    /**
     * Eliminar un libro por ID
     *
     * @param booksId El ID del libro a eliminar
     * @throws BusinessLogicException si el libro tiene autores asociados
     */
    public void deleteBook(Long booksId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "Inicia proceso de borrar el libro con id = {0}", booksId);
        List<AuthorEntity> authors = getBook(booksId).getAuthors();
        if (authors != null && !authors.isEmpty()) {
            throw new BusinessLogicException("No se puede borrar el libro con id = " + booksId + " porque tiene autores asociados");
        }
        persistence.delete(booksId);
        LOGGER.log(Level.INFO, "Termina proceso de borrar el libro con id = {0}", booksId);
    }

    /**
     * Verifica que el ISBN no sea invalido.
     *
     * @param isbn a verificar
     * @return true si el ISBN es valido.
     */
    private boolean validateISBN(String isbn) {
        return !(isbn == null || isbn.isEmpty());
    }
}
