package jp.co.metateam.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface BookMstRepository extends JpaRepository<BookMst, Long> {

	@Query(value = "SELECT * FROM book_mst LIMIT 1000", nativeQuery = true) // 書籍データを取るためにアクセス中
	List<BookMst> findLimitedBook();

	@Query(value = "SELECT * FROM book_mst WHERE isbn = ?1 AND deleted_at IS NULL", nativeQuery = true)
	BookMst findByIsbn(String isbn);

	@Query(value = "SELECT * FROM book_mst WHERE id = ?1 AND deleted_at IS NULL", nativeQuery = true)
	Optional<BookMst> findtById(Long id);
}
