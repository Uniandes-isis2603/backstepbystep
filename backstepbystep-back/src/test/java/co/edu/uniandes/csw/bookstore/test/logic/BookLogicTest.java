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
package co.edu.uniandes.csw.bookstore.test.logic;

import co.edu.uniandes.csw.bookstore.ejb.BookLogic;
import co.edu.uniandes.csw.bookstore.entities.AuthorEntity;
import co.edu.uniandes.csw.bookstore.entities.BookEntity;
import co.edu.uniandes.csw.bookstore.persistence.BookPersistence;
import co.edu.uniandes.csw.bookstore.entities.EditorialEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de Books
 *
 * @author ISIS2603
 */
@RunWith(Arquillian.class)
public class BookLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private BookLogic bookLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private List<BookEntity> bookData = new ArrayList<>();

    private List<EditorialEntity> editorialData = new ArrayList();
    
    private List<AuthorEntity> authorData = new ArrayList();

    /**
     * @return Devuelve el jar que Arquillian va a desplegar en Payara embebido.
     * El jar contiene las clases, el descriptor de la base de datos y el
     * archivo beans.xml para resolver la inyección de dependencias.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(BookEntity.class.getPackage())
                .addPackage(BookLogic.class.getPackage())
                .addPackage(BookPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Configuración inicial de la prueba.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Limpia las tablas que están implicadas en la prueba.
     */
    private void clearData() {
        em.createQuery("delete from BookEntity").executeUpdate();
        em.createQuery("delete from EditorialEntity").executeUpdate();
        em.createQuery("delete from AuthorEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            EditorialEntity editorial = factory.manufacturePojo(EditorialEntity.class);
            em.persist(editorial);
            editorialData.add(editorial);
        }
        for (int i = 0; i < 3; i++) {
            BookEntity entity = factory.manufacturePojo(BookEntity.class);
            entity.setEditorial(editorialData.get(0));

            em.persist(entity);
            bookData.add(entity);
        }
        for (int i = 0; i < 3; i++) {
            AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
            em.persist(entity);
            authorData.add(entity);
        }
        em.persist(authorData.get(0));
        authorData.get(0).getBooks().add(bookData.get(1));
        bookData.get(1).getAuthors().add(authorData.get(0));
    }

    /**
     * Prueba para crear un Book
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test
    public void createBookTest() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setEditorial(editorialData.get(0));
        newEntity.setAuthors(authorData);
        BookEntity result = bookLogic.createBook(newEntity);
        Assert.assertNotNull(result);
        BookEntity entity = em.find(BookEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getDescription(), entity.getDescription());
        Assert.assertEquals(newEntity.getIsbn(), entity.getIsbn());
        Assert.assertEquals(newEntity.getImage(), entity.getImage());
    }

    /**
     * Prueba para crear un Book con ISBN inválido
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookTestConISBNInvalido() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setEditorial(editorialData.get(0));
        newEntity.setIsbn("");
        bookLogic.createBook(newEntity);
    }

    /**
     * Prueba para crear un Book con ISBN inválido
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookTestConISBNInvalido2() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setEditorial(editorialData.get(0));
        newEntity.setIsbn(null);
        bookLogic.createBook(newEntity);
    }

    /**
     * Prueba para crear un Book con ISBN existente.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookTestConISBNExistente() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setEditorial(editorialData.get(0));
        newEntity.setIsbn(bookData.get(0).getIsbn());
        bookLogic.createBook(newEntity);
    }

    /**
     * Prueba para crear un Book con una editorial que no existe.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookTestConEditorialInexistente() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        EditorialEntity editorialEntity = new EditorialEntity();
        editorialEntity.setId(Long.MIN_VALUE);
        newEntity.setEditorial(editorialEntity);
        bookLogic.createBook(newEntity);
    }

    /**
     * Prueba para crear un Book con editorial en null.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookTestConNullEditorial() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setEditorial(null);
        bookLogic.createBook(newEntity);
    }
    
    /**
     * Prueba para crear un Book con nombre corto.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConNombreCorto() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setName("Odisea");
        bookLogic.createBook(newEntity);
    }
    
    /**
     * Prueba para crear un Book con descripción corta.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConDescripcionCorta() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setDescription("Curioso");
        bookLogic.createBook(newEntity);
    }
    
    /**
     * Prueba para crear un Book sin imagen.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConImagenVacia() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setImage("");
        bookLogic.createBook(newEntity);
    }
    
    /**
     * Prueba para crear un Book con lista autores en null.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConNullListAuthor() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        newEntity.setAuthors(null);
        bookLogic.createBook(newEntity);
    }
    
    /**
     * Prueba para crear un Book con un autor null.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConNullAuthor() throws BusinessLogicException {
        BookEntity newEntity = factory.manufacturePojo(BookEntity.class);
        authorData.add(null);
        newEntity.setAuthors(authorData);
        bookLogic.createBook(newEntity);
    }

    /**
     * Prueba para consultar la lista de Books.
     */
    @Test
    public void getBooksTest() {
        List<BookEntity> list = bookLogic.getBooks();
        Assert.assertEquals(bookData.size(), list.size());
        for (BookEntity entity : list) {
            boolean found = false;
            for (BookEntity storedEntity : bookData) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Book.
     */
    @Test
    public void getBookTest() {
        BookEntity entity = bookData.get(0);
        BookEntity resultEntity = bookLogic.getBook(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
        Assert.assertEquals(entity.getDescription(), resultEntity.getDescription());
        Assert.assertEquals(entity.getIsbn(), resultEntity.getIsbn());
        Assert.assertEquals(entity.getImage(), resultEntity.getImage());
    }

    /**
     * Prueba para actualizar un Book.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test
    public void updateBookTest() throws BusinessLogicException {
        BookEntity entity = bookData.get(0);
        BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
        pojoEntity.setId(entity.getId());
        bookLogic.updateBook(pojoEntity.getId(), pojoEntity);
        BookEntity resp = em.find(BookEntity.class, entity.getId());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getName(), resp.getName());
        Assert.assertEquals(pojoEntity.getDescription(), resp.getDescription());
        Assert.assertEquals(pojoEntity.getIsbn(), resp.getIsbn());
        Assert.assertEquals(pojoEntity.getImage(), resp.getImage());
    }

    /**
     * Prueba para actualizar un Book con ISBN inválido.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void updateBookConISBNInvalidoTest() throws BusinessLogicException {
        BookEntity entity = bookData.get(0);
        BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
        pojoEntity.setIsbn("");
        pojoEntity.setId(entity.getId());
        bookLogic.updateBook(pojoEntity.getId(), pojoEntity);
    }

    /**
     * Prueba para actualizar un Book con ISBN inválido.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void updateBookConISBNInvalidoTest2() throws BusinessLogicException {
        BookEntity entity = bookData.get(0);
        BookEntity pojoEntity = factory.manufacturePojo(BookEntity.class);
        pojoEntity.setIsbn(null);
        pojoEntity.setId(entity.getId());
        bookLogic.updateBook(pojoEntity.getId(), pojoEntity);
    }

    /**
     * Prueba para eliminar un Book.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test
    public void deleteBookTest() throws BusinessLogicException {
        BookEntity entity = bookData.get(0);
        bookLogic.deleteBook(entity.getId());
        BookEntity deleted = em.find(BookEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para eliminar un Book.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteBookWithAuthorTest() throws BusinessLogicException {
        BookEntity entity = bookData.get(1);
        bookLogic.deleteBook(entity.getId());
    }
}
