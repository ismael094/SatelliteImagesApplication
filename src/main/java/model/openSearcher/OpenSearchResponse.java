package model.openSearcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.products.ProductDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "feed")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenSearchResponse{

    @JsonProperty("opensearch:totalResults")
    private int numOfProducts = 0;
    @JsonProperty("opensearch:startIndex")
    private int startIndex = 0;
    @JsonProperty("opensearch:itemsPerPage")
    private int rows = 0;
    @JsonProperty("entry")
    private List<ProductDTO> products = new ArrayList<>();

    /**
     * Get number of total products in the service
     * @return total products
     */
    public int getNumOfProducts() {
        return numOfProducts;
    }

    /**
     * Set number of total products in the service
     * @param numOfProducts total products
     */
    public void setNumOfProducts(int numOfProducts) {
        this.numOfProducts = numOfProducts;
    }

    /**
     * Get Products of response
     * @return list of products
     */
    public List<ProductDTO> getProducts() {
        return products;
    }

    /**
     * set products
     * @param products products
     */
    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    /**
     * Get first product to get
     * @return start index
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Set first product to get
     * @param startIndex  start index
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Get rows per page
     * @return page of response
     */
    public int getRows() {
        return rows;
    }

    /**
     * Set rows per page
     * @param rows rows per page
     */
    public void setRows(int rows) {
        this.rows = rows;
    }
}
