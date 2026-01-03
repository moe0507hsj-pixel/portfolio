package jp.co.metateam.library.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {

    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService) {
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();// 保存されたデータを書籍テーブルから取得。リストで受け取っている

        model.addAttribute("bookMstList", bookMstList);// htmlに渡すmodelはhtmlへ

        return "book/index";
    }

    @GetMapping("/book/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        // データベースから指定されたIDの本情報を取得
        BookMstDto bookMstDto = bookMstService.findById(id); // bookMstServiceはデータベースから本を取得するサービス

        if (bookMstDto == null) {
            List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();// 保存されたデータを書籍テーブルから取得。リストで受け取っている

            model.addAttribute("bookMstList", bookMstList);
            model.addAttribute("noExist", "該当する書籍が存在しません");
            return "book/index";
        }
        model.addAttribute("bookMstDto", bookMstDto);
        return "book/edit";

    }

    @PostMapping("/book/edit")
    public String edit(@Valid @ModelAttribute BookMstDto bookMstDto, Long id, String title, String isbn, Model model,
            RedirectAttributes ra) {
        // modelattributeは画面とコントローラーをつなぐ

        try {
            BookMstDto originBookMstDto = bookMstService.findById(bookMstDto.getId());
            // DBから書籍情報を取ってきている originがDBからきたやつ？

            if (originBookMstDto == null) {
                // model.addAttribute("isShowPopUp", "true");
                model.addAttribute("noExist", "該当する書籍が存在しません");
                return "book/edit";
            }
            // DBから該当書籍がなくなっていたらポップアップ表示

            boolean isTitleChanged = !bookMstDto.getTitle().equals(originBookMstDto.getTitle());// 異なっていたらtrue
            boolean isIsbnChanged = !bookMstDto.getIsbn().equals(originBookMstDto.getIsbn());

            if (!isTitleChanged && !isIsbnChanged) {
                // model.addAttribute("isShowPopUp", "true");
                model.addAttribute("noChange", "変更が行われていません");
                return "book/edit"; // 変更がない場合はそのまま編集画面に戻す
            }
            // バリデーションチェック
            String titleExist = bookMstDto.getTitle();
            String isbnExist = bookMstDto.getIsbn();
            Long idExist = bookMstDto.getId();

            List<String> errTitleList = isValidTitle(titleExist);

            List<String> errIsbnList = isValidIsbn(isbnExist);

            if (errIsbnList.isEmpty()) {
                errIsbnList = isIsbnDuplicate(isbnExist);
            }

            if (!errTitleList.isEmpty() || !errIsbnList.isEmpty()) {
                model.addAttribute("errTitle", errTitleList);
                model.addAttribute("errIsbn", errIsbnList);
                return "book/edit";
            }
            bookMstService.updateBook(id, title, isbn);
            // BookMstDto updatedBook = bookMstService.updateBook();
            // model.addAttribute("book", updatedBook);

            // model.addAttribute("success", "書籍情報が正常に更新されました");

            return "redirect:/book/index";

        } catch (Exception e) {
            log.error(e.getMessage());

            ra.addFlashAttribute("bookMstDto", bookMstDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.bookMstDto");

            return "book/edit";
        }

    }

    @GetMapping("/book/add") // add htmlのgetからとんでくる
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }

    @PostMapping("/book/add")
    public String registBook(@Valid @ModelAttribute BookMstDto bookMstDto, Model model, RedirectAttributes ra) {
        try {

            String titleExist = bookMstDto.getTitle();
            String IsbnExist = bookMstDto.getIsbn();

            List<String> errTitleList = isValidTitle(titleExist);

            List<String> errIsbnList = isValidIsbn(IsbnExist);

            if (errIsbnList.isEmpty()) {
                errIsbnList = isIsbnDuplicate(IsbnExist);
            }

            if (!errTitleList.isEmpty() || !errIsbnList.isEmpty()) {
                model.addAttribute("errTitle", errTitleList);
                model.addAttribute("errIsbn", errIsbnList);
                return "book/add";
            }

            bookMstService.save(bookMstDto);

            return "redirect:/book/index";// if処理を反映して返している

        } catch (Exception e) {
            log.error(e.getMessage());

            ra.addFlashAttribute("bookMstDto", bookMstDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.bookMstDto");

            return "book/add";
        }
    }

    private List<String> isIsbnDuplicate(String isbnExist) {
        BookMst isbnDuplicate = null;
        List<String> errIsbnList = new ArrayList<>();
        isbnDuplicate = this.bookMstService.findByIsbn(isbnExist);

        if (isbnDuplicate != null) {
            errIsbnList.add("登録済みのISBNです");
            return errIsbnList;
        }
        return errIsbnList;
    }

    private List<String> isValidIsbn(String isbnExist) {
        List<String> errIsbnList = new ArrayList<>();

        if (StringUtils.isEmpty(isbnExist)) {
            errIsbnList.add("ISBNは必須です");
            return errIsbnList;
        }

        if (isbnExist != null && isbnExist.length() != 13) {
            errIsbnList.add("ISBNは13文字で入力してください");
        }

        if (isbnExist != null && !isbnExist.matches("^[0-9]+$")) {
            errIsbnList.add("ISBNの形式が不正です");
        }
        return errIsbnList;
    }

    private List<String> isValidTitle(String titleExist) {
        List<String> errTitleList = new ArrayList<>();

        if (StringUtils.isEmpty(titleExist)) {
            errTitleList.add("書籍名は必須です");
            return errTitleList;
        }

        if (titleExist != null && titleExist.length() > 255) {
            errTitleList.add("書籍名は255文字以内で入力してください");
        }
        return errTitleList;
    }
}
