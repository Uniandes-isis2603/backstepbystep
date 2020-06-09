package co.edu.uniandes.csw.bookstore.resources;

import co.edu.uniandes.csw.bookstore.dtos.OrganizationDTO;
import co.edu.uniandes.csw.bookstore.dtos.OrganizationDetailDTO;
import co.edu.uniandes.csw.bookstore.ejb.OrganizationLogic;
import co.edu.uniandes.csw.bookstore.entities.OrganizationEntity;
import co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.bookstore.mappers.BusinessLogicExceptionMapper;
import co.edu.uniandes.csw.bookstore.mappers.WebApplicationExceptionMapper;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que implementa el recurso "organizations".
 *
 * @author ISIS2603
 * @version 1.0
 */
@Path("organizations")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class OrganizationResource {

    private static final Logger LOGGER = Logger.getLogger(OrganizationResource.class.getName());

    @Inject
    private OrganizationLogic organizationLogic;

    private static final String MSG1 = "El recurso /organizations/";
    private static final String MSG2 = " no existe.";

    /**
     * Crea una nueva organization con la informacion que se recibe en el cuerpo
     * de la petición y se regresa un objeto identico con un id auto-generado
     * por la base de datos.
     *
     * @param organization {@link OrganizationDTO} - La organization que se
     * desea guardar.
     * @return JSON {@link OrganizationDTO} - La organization guardada con el
     * atributo id autogenerado.
     * @throws BusinessLogicException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando ya existe la organization.
     */
    @POST
    public OrganizationDTO createOrganization(OrganizationDTO organization) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "OrganizationResource createOrganization: input: {0}", organization);
        OrganizationDTO nuevoOrganizationDTO = new OrganizationDTO(organizationLogic.createOrganization(organization.toEntity()));
        LOGGER.log(Level.INFO, "OrganizationResource createOrganization: output: {0}", nuevoOrganizationDTO);
        return nuevoOrganizationDTO;
    }

    /**
     * Busca y devuelve todos las organizaciones que existen en la aplicacion.
     *
     * @return JSONArray {@link OrganizationDTO} - Las organizaciones
     * encontradas en la aplicación. Si no hay ninguna retorna una lista vacía.
     */
    @GET
    public List<OrganizationDetailDTO> getOrganizations() {
        LOGGER.info("OrganizationResource getOrganizations: input: void");
        List<OrganizationDetailDTO> listaOrganizations = listOrganizationEntity2DetailDTO(organizationLogic.getOrganizations());
        LOGGER.log(Level.INFO, "OrganizationResource getOrganizations: output: {0}", listaOrganizations);
        return listaOrganizations;
    }

    /**
     * Busca la organization con el id asociado recibido en la URL y lo
     * devuelve.
     *
     * @param organizationsId Identificador de la organization que se esta
     * buscando. Este debe ser una cadena de dígitos.
     * @return JSON {@link OrganizationDetailDTO} - La organization buscada
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra la organization.
     */
    @GET
    @Path("{organizationsId: \\d+}")
    public OrganizationDetailDTO getOrganization(@PathParam("organizationsId") Long organizationsId) {
        LOGGER.log(Level.INFO, "OrganizationResource getOrganization: input: {0}", organizationsId);
        OrganizationEntity organizationEntity = organizationLogic.getOrganization(organizationsId);
        if (organizationEntity == null) {
            throw new WebApplicationException(MSG1 + organizationsId + MSG2, 404);
        }
        OrganizationDetailDTO organizationDetailDTO = new OrganizationDetailDTO(organizationEntity);
        LOGGER.log(Level.INFO, "OrganizationResource getOrganization: output: {0}", organizationDetailDTO);
        return organizationDetailDTO;
    }

    /**
     * Actualiza la organization con el id recibido en la URL con la información
     * que se recibe en el cuerpo de la petición.
     *
     * @param organizationsId Identificador de la organization que se desea
     * actualizar. Este debe ser una cadena de dígitos.
     * @param organization {@link OrganizationDTO} La organization que se desea
     * guardar.
     * @return JSON {@link OrganizationDTO} - La organization guardada.
     * @throws WebApplicationException {@link WebApplicationExceptionMapper} -
     * Error de lógica que se genera cuando no se encuentra la organization a
     * actualizar.
     */
    @PUT
    @Path("{organizationsId: \\d+}")
    public OrganizationDTO updateOrganization(@PathParam("organizationsId") Long organizationsId, OrganizationDTO organization) {
        LOGGER.log(Level.INFO, "OrganizationResource updateOrganization: input: id: {0} , organization: {1}", new Object[]{organizationsId, organization});
        organization.setId(organizationsId);
        if (organizationLogic.getOrganization(organizationsId) == null) {
            throw new WebApplicationException(MSG1 + organizationsId + MSG2, 404);
        }
        OrganizationDetailDTO detailDTO = new OrganizationDetailDTO(organizationLogic.updateOrganization(organizationsId, organization.toEntity()));
        LOGGER.log(Level.INFO, "OrganizationResource updateOrganization: output: {0}", detailDTO);
        return detailDTO;
    }

    /**
     * Borra la organization con el id asociado recibido en la URL.
     *
     * @param organizationsId Identificador de la organization que se desea
     * borrar. Este debe ser una cadena de dígitos.
     * @throws WebApplicationException {@link BusinessLogicExceptionMapper} -
     * Error de lógica que se genera cuando no se puede eliminar la
     * organization.
     * @throws co.edu.uniandes.csw.bookstore.exceptions.BusinessLogicException
     * Error de lógica que se genera cuando la organizacion tiene un premio
     * asociado.
     */
    @DELETE
    @Path("{organizationsId: \\d+}")
    public void deleteOrganization(@PathParam("organizationsId") Long organizationsId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "OrganizationResource deleteOrganization: input: {0}", organizationsId);
        if (organizationLogic.getOrganization(organizationsId) == null) {
            throw new WebApplicationException(MSG1 + organizationsId + MSG2, 404);
        }
        organizationLogic.deleteOrganization(organizationsId);
        LOGGER.info("OrganizationResource deleteOrganization: output: void");
    }

    /**
     * Convierte una lista de entidades a DTO.
     *
     * Este método convierte una lista de objetos OrganizationEntity a una lista
     * de objetos OrganizationDetailDTO (json)
     *
     * @param entityList corresponde a la lista de organizaciones de tipo Entity
     * que vamos a convertir a DTO.
     * @return la lista de organizaciones en forma DTO (json)
     */
    private List<OrganizationDetailDTO> listOrganizationEntity2DetailDTO(List<OrganizationEntity> entityList) {
        List<OrganizationDetailDTO> list = new ArrayList<>();
        for (OrganizationEntity entity : entityList) {
            list.add(new OrganizationDetailDTO(entity));
        }
        return list;
    }
}
