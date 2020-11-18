package services.database;

import java.util.List;

public interface DAO<E> {
    List<E> getCollection();
    List<E> find(E dao);

    E findFirst(E dao);

    void save(E dao);

    void delete(E dao);

    void delete(List<E> dao);

}
