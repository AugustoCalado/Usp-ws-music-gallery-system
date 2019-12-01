package ws.music.gallery.system.converter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ws.music.gallery.system.domain.dto.MusicalnstrumentDTO;
import ws.music.gallery.system.domain.dto.ProductDTO;
import ws.music.gallery.system.domain.dto.StoreDTO;
import ws.music.gallery.system.enums.TypeProductAndBusiness;
import ws.music.gallery.system.util.OntologyPropertyAndResourceUtils;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicalInstrumentOntologyEntitiesConverter extends ProductOntologyEntitiesConverter {

    @Autowired
    private OntModel musicGalleryOntologyModel;

    @Autowired
    private StoreOntologyEntitiesConverter storeOntologyEntitiesConverter;

    @Value("${music.gallery.uri}")
    private String MUSIC_GALLERY_URI;

    @Override
    public Individual productDTOToindividual(ProductDTO productDTO) {
        return musicGalleryOntologyModel.getIndividual(productDTO.getURI());
    }

    public ProductDTO individualToProductDTO(Individual productIndividual) {
        Resource storeResource = productIndividual.getProperty(OntologyPropertyAndResourceUtils.soldByStore).getResource();
        StoreDTO storeDTO = storeOntologyEntitiesConverter.resourceToStoreDTO(storeResource);

        if (!productIndividual.hasProperty(RDF.type, new ResourceImpl(OntologyPropertyAndResourceUtils.recordPlayer))) {
            return checkNextIndvToDto(productIndividual).orElse(null);
        }

        return MusicalnstrumentDTO.builder()
                .name(productIndividual.getProperty(OntologyPropertyAndResourceUtils.name).getLiteral().getValue().toString())
                .typeProductAndBusiness(TypeProductAndBusiness.valueOf(productIndividual.getProperty(OntologyPropertyAndResourceUtils.typeIs).getResource().getLocalName().toUpperCase()))
                .brand(productIndividual.getProperty(OntologyPropertyAndResourceUtils.brand).getLiteral().getValue().toString())
                .price(productIndividual.getProperty(OntologyPropertyAndResourceUtils.price).getDouble())
                .soldByStore(storeDTO)
                .URI(productIndividual.getURI())

                .typeOfInstrument(productIndividual.getProperty(OntologyPropertyAndResourceUtils.typeOfInstrument).getLiteral().getValue().toString())
                .isSecondHand(Boolean.parseBoolean(productIndividual.getProperty(OntologyPropertyAndResourceUtils.isSecondHand).getLiteral().getValue().toString()))
                .build();
    }


    public Resource productDTOToResource(ProductDTO productDTO) {
        return musicGalleryOntologyModel.getResource(productDTO.getURI());
    }

    public ProductDTO resourceToProductDTO(Resource productResource) {
        Resource storeResource = productResource.getProperty(OntologyPropertyAndResourceUtils.soldByStore).getResource();
        StoreDTO storeDTO = storeOntologyEntitiesConverter.resourceToStoreDTO(storeResource);

        if (!productResource.hasProperty(RDF.type, new ResourceImpl(OntologyPropertyAndResourceUtils.recordPlayer))) {
            return checkNextResourceToDto(productResource).orElse(null);
        }

        return MusicalnstrumentDTO.builder()
                .name(productResource.getProperty(OntologyPropertyAndResourceUtils.name).getLiteral().getValue().toString())
                .typeProductAndBusiness(TypeProductAndBusiness.valueOf(productResource.getProperty(OntologyPropertyAndResourceUtils.typeIs).getResource().getLocalName().toUpperCase()))
                .brand(productResource.getProperty(OntologyPropertyAndResourceUtils.brand).getLiteral().getValue().toString())
                .price(productResource.getProperty(OntologyPropertyAndResourceUtils.price).getDouble())
                .soldByStore(storeDTO)
                .URI(productResource.getURI())

                .typeOfInstrument(productResource.getProperty(OntologyPropertyAndResourceUtils.typeOfInstrument).getLiteral().getValue().toString())
                .isSecondHand(Boolean.parseBoolean(productResource.getProperty(OntologyPropertyAndResourceUtils.isSecondHand).getLiteral().getValue().toString()))
                .build();
    }

    @Override
    protected Map<String, String> getPropertiesAndTypes() {
        Map<String, String> mapOfPropertiesAndTypes = super.getPropertiesAndTypes();
        mapOfPropertiesAndTypes.put("typeOfInstrument", MUSIC_GALLERY_URI + "typeOfInstrument");
        mapOfPropertiesAndTypes.put("isSecondHand", MUSIC_GALLERY_URI + "isSecondHand");
        return mapOfPropertiesAndTypes;
    }
}
