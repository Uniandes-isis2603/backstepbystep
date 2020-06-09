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

import co.edu.uniandes.csw.bookstore.ejb.AuthorLogic;
import co.edu.uniandes.csw.bookstore.entities.AuthorEntity;
import co.edu.uniandes.csw.bookstore.entities.BookEntity;
import co.edu.uniandes.csw.bookstore.entities.PrizeEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.persistence.AuthorPersistence;
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
 * Pruebas de logica de Authors
 *
 * @author ISIS2603
 */
@RunWith(Arquillian.class)
public class AuthorLogicTest {

    private PodamFactory factory = new PodamFactoryImpl();

    @Inject
    private AuthorLogic authorLogic;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private List<AuthorEntity> data = new ArrayList<>();

    /**
     * @return Devuelve el jar que Arquillian va a desplegar en Payara embebido.
     * El jar contiene las clases, el descriptor de la base de datos y el
     * archivo beans.xml para resolver la inyección de dependencias.
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(AuthorEntity.class.getPackage())
                .addPackage(AuthorLogic.class.getPackage())
                .addPackage(AuthorPersistence.class.getPackage())
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
        em.createQuery("delete from PrizeEntity").executeUpdate();
        em.createQuery("delete from BookEntity").executeUpdate();
        em.createQuery("delete from AuthorEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AuthorEntity entity = factory.manufacturePojo(AuthorEntity.class);
            em.persist(entity);
            entity.setBooks(new ArrayList<>());
            data.add(entity);
        }
        AuthorEntity author = data.get(2);
        BookEntity entity = factory.manufacturePojo(BookEntity.class);
        entity.getAuthors().add(author);
        em.persist(entity);
        author.getBooks().add(entity);

        PrizeEntity prize = factory.manufacturePojo(PrizeEntity.class);
        prize.setAuthor(data.get(1));
        em.persist(prize);
        data.get(1).getPrizes().add(prize);
    }

    /**
     * Prueba para crear un Author.
     */
    @Test
    public void createAuthorTest() throws BusinessLogicException {
        AuthorEntity newEntity = factory.manufacturePojo(AuthorEntity.class);
        AuthorEntity result = authorLogic.createAuthor(newEntity);
        Assert.assertNotNull(result);
        AuthorEntity entity = em.find(AuthorEntity.class, result.getId());
        Assert.assertEquals(newEntity.getId(), entity.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getBirthDate(), entity.getBirthDate());
    }
    
    /**
     * Prueba para crear un Autor con nombre corto.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConNombreCorto() throws BusinessLogicException {
        AuthorEntity newEntity = factory.manufacturePojo(AuthorEntity.class);
        newEntity.setName("Oh");
        authorLogic.createAuthor(newEntity);
    }
    
    /**
     * Prueba para crear un Autor con descripción corta.
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void createBookConDescripcionCorta() throws BusinessLogicException {
        AuthorEntity newEntity = factory.manufacturePojo(AuthorEntity.class);
        newEntity.setDescription("Oh");
        authorLogic.createAuthor(newEntity);
    }

    /**
     * Prueba para consultar la lista de Authors.
     */
    @Test
    public void getAuthorsTest() {
        List<AuthorEntity> list = authorLogic.getAuthors();
        Assert.assertEquals(data.size(), list.size());
        for (AuthorEntity entity : list) {
            boolean found = false;
            for (AuthorEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Author.
     */
    @Test
    public void getAuthorTest() {
        AuthorEntity entity = data.get(0);
        AuthorEntity resultEntity = authorLogic.getAuthor(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getName(), resultEntity.getName());
        Assert.assertEquals(entity.getBirthDate(), resultEntity.getBirthDate());
    }

    /**
     * Prueba para actualizar un Author.
     */
    @Test
    public void updateAuthorTest() {
        AuthorEntity entity = data.get(0);
        AuthorEntity pojoEntity = factory.manufacturePojo(AuthorEntity.class);

        pojoEntity.setId(entity.getId());

        authorLogic.updateAuthor(pojoEntity.getId(), pojoEntity);

        AuthorEntity resp = em.find(AuthorEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        Assert.assertEquals(pojoEntity.getName(), resp.getName());
        Assert.assertEquals(pojoEntity.getBirthDate(), resp.getBirthDate());
    }

    /**
     * Prueba para eliminar un Author
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test
    public void deleteAuthorTest() throws BusinessLogicException {
        AuthorEntity entity = data.get(0);
        authorLogic.deleteAuthor(entity.getId());
        AuthorEntity deleted = em.find(AuthorEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para eliminar un Author asociado a un libro
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteAuthorConLibroTest() throws BusinessLogicException {
        authorLogic.deleteAuthor(data.get(2).getId());
    }

    /**
     * Prueba para eliminar un Author asociado a un premio
     *
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     */
    @Test(expected = BusinessLogicException.class)
    public void deleteAuthorConPremioTest() throws BusinessLogicException {
        authorLogic.deleteAuthor(data.get(1).getId());
    }
}
