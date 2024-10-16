package service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import model.User;
import repository.interfaces.AdminRepository;
import repository.interfaces.ClientRepository;
import repository.interfaces.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private User user;
    private List<User> userList;

    @Before
    public void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    public void testAddAuthor() {
        Author author = new Author();
        authorService.addAuthor(author);
        verify(authorRepository, times(1)).addAuthor(author);
    }

    @Test
    public void testUpdateAuthor() {
        Author author = new Author();
        authorService.updateAuthor(author);
        verify(authorRepository, times(1)).updateAuthor(author);
    }

    @Test
    public void testDeleteAuthor() {
        Long authorId = 1L;
        authorService.deleteAuthor(authorId);
        verify(authorRepository, times(1)).deleteAuthor(authorId);
    }

    @Test
    public void testGetAllAuthors() {
        List<Author> authors = Arrays.asList(new Author(), new Author());
        when(authorRepository.getAllAuthors()).thenReturn(authors);

        List<Author> result = authorService.getAllAuthors();
        assertEquals(2, result.size());
        verify(authorRepository, times(1)).getAllAuthors();
    }

    @Test
    public void testGetAuthorById() {
        Long authorId = 1L;
        Author author = new Author();
        when(authorRepository.getAuthorById(authorId)).thenReturn(author);

        Author result = authorService.getAuthorById(authorId);
        assertEquals(author, result);
        verify(authorRepository, times(1)).getAuthorById(authorId);
    }



    @Test
    public void testCountAuthors() {
        when(authorRepository.countAuthors()).thenReturn(5L);

        long result = authorService.countAuthors();
        assertEquals(5, result);
        verify(authorRepository, times(1)).countAuthors();
    }



    @Test
    public void testGetAllAuthorsWithPagination() {

        int pageNumber = 1;
        int pageSize = 5;
        List<Author> authors = Arrays.asList(new Author(), new Author());
        when(authorRepository.getAllAuthors(pageNumber, pageSize)).thenReturn(authors);

        List<Author> result = authorService.getAllAuthors(pageNumber, pageSize);
        assertEquals(2, result.size());
        verify(authorRepository, times(1)).getAllAuthors(pageNumber, pageSize);
    }
}
