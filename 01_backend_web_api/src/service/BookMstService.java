package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;

    @Autowired
    public BookMstService(BookMstRepository bookMstRepository) {
        this.bookMstRepository = bookMstRepository;
    }

    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();// インスタンス化43行目までいったらadd?

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {// 取ってきた書籍の数分繰り返す
            BookMst book = books.get(i);// bookmstの型からbookmutdtoにセットしなおす(bookmstだと良くない)
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }

    public BookMst findByIsbn(String isbn) {
        return this.bookMstRepository.findByIsbn(isbn);
    }

    @Transactional
    public void save(BookMstDto bookMstDto) {
        try {
            // BookMstDtoからBookMstへの変換
            BookMst bookMst = new BookMst();

            bookMst.setTitle(bookMstDto.getTitle());
            bookMst.setIsbn(bookMstDto.getIsbn());

            // データベースへの保存
            this.bookMstRepository.save(bookMst);

        } catch (Exception e) {
            throw e;
        }
    }

    public BookMstDto findById(Long id) {
        BookMst book = bookMstRepository.findById(id).orElse(null);
        if (book == null)
            return null;
        BookMstDto dto = new BookMstDto(); // DTO を作成
        dto.setId(book.getId()); // 値をコピー
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());

        return dto; // DTO を返す
    }

    public void updateBook(Long id, String title, String isbn) {

        Optional<BookMst> bookMstOptional = bookMstRepository.findById(id);
        if (bookMstOptional.isPresent()) {

            BookMst bookMst = bookMstOptional.get();
            // BookMst bookMstOptinal = new BookMst();
            bookMst.setTitle(title);
            bookMst.setIsbn(isbn);

            this.bookMstRepository.save(bookMst);
        } else {

            return;
        }

    }

}
