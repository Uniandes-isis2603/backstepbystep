delete from ReviewEntity;
delete from BookEntity_AuthorEntity;
delete from AuthorEntity;
delete from BookEntity;
delete from EditorialEntity;

insert into EditorialEntity (id, name) values (100,'Oveja Negra');
insert into EditorialEntity (id, name) values (200,'Siruela');

insert into BookEntity (id, name, isbn, image, description, publishDate, editorial_id) values (100, 'The Lord of the Rings', '930330149-8', 'https://images-na.ssl-images-amazon.com/images/I/516GyHY9p6L.jpg', 'Supplement R Tympanic Membrane with Synth Sub, Via Opening', '8/20/1996',100);
insert into BookEntity (id, name, isbn, image, description, publishDate, editorial_id) values (200, 'Harry Potter and the Sorcerer´s Stone', '507119915-7', 'http://m.cdn.blog.hu/ko/kockagyar/image/harry_potter_poster/harry_potter_1.jpg', 'Occlusion of Right Femoral Artery, Percutaneous Approach', '2/19/2014',100);
insert into BookEntity (id, name, isbn, image, description, publishDate, editorial_id) values (300, 'A Game of Thrones', '279453624-9', 'https://cdn.waterstones.com/bookjackets/large/9780/0075/9780007548231.jpg', 'Removal of Spacer from T-lum Jt, Perc Approach', '4/7/1998',100);
insert into BookEntity (id, name, isbn, image, description, publishDate, editorial_id) values (400, 'The Winds of Winter', '744706866-7', 'http://www.darkmediaonline.com/wp-content/uploads/2013/01/WindsofWinter.jpg', 'Reposition Left Femoral Shaft, Perc Endo Approach', '10/10/1998',200);
insert into BookEntity (id, name, isbn, image, description, publishDate, editorial_id) values (500, 'The Slow Regard of Silent Things', '260760424-9', 'http://www.patrickrothfuss.com/images/page/cover-slow-regard_277.jpg', 'Supplement Lower Artery with Autol Sub, Perc Approach', '5/9/2013',200);
insert into BookEntity (id, name, isbn, image, description, publishDate, editorial_id) values (600, 'Harry Potter and the Philosopher´s Stone', '260760424-9', 'https://katemacdonalddotnet.files.wordpress.com/2015/11/potter-1-4.jpg', 'disse accumsan tortor quis turp Perc Approach', '5/9/2013',200);

insert into AuthorEntity (id, name,  image, birthDate, description) values (100,'J.K. Rowling', 'https://d22uy85tnpcynx.cloudfront.net/2020/04/JKRowling_Photo-1-1024x608.jpg', '4/7/1965','Joanne  Rowling, OBE, FRSL, pen names J. K. Rowling and Robert Galbraith, is a British novelist, screenwriter and film producer best known as the author of the Harry Potter fantasy series.');
insert into AuthorEntity (id, name,  image, birthDate, description) values (200, 'J. R. R. Tolkien', 'http://www.biografiasyvidas.com/biografia/t/fotos/tolkien.jpg', '01/03/1892','Joanne  Rowling, OBE, FRSL, pen names J. K. Rowling and Robert Galbraith, is a British novelist, screenwriter and film producer best known as the author of the Harry Potter fantasy series.');
insert into AuthorEntity (id, name,  image, birthDate, description) values (300, 'George R. R. Martin','http://static1.businessinsider.com/image/54d3c832eab8eabe028b4569-960/george-rr-martin-2011.jpg','09/20/1948', 'asA smkcxm slklkc n names J. K. Rowling and Robert Galbraith, is a British novelist');
insert into AuthorEntity (id, name,  image, birthDate, description) values (400,'Patrick Rothfuss','http://librista.es/blog/wp-content/uploads/2014/05/patrick-rothfuss.jpg/5x10','06/06/1973','Joanne  Rowling, OBE, FRSL, pen names J. K. Rowling and Robert Galbraith, is a British novelist, screenwriter and film producer best known as the author of the Harry Potter fantasy series.');

insert into BookEntity_AuthorEntity (books_id,authors_id ) values (100,200);
insert into BookEntity_AuthorEntity (books_id,authors_id ) values (200,100); 
insert into BookEntity_AuthorEntity (books_id,authors_id ) values (300,300);
insert into BookEntity_AuthorEntity (books_id,authors_id ) values (400,300); 
insert into BookEntity_AuthorEntity (books_id,authors_id ) values (500,400);   
insert into BookEntity_AuthorEntity (books_id,authors_id ) values (600,100);      

insert into ReviewEntity  (id, name,  description, source, book_id) values (100,' ', 'This isnot really like other books, even its imitators, though the best of them are similarly long, variable in pace and diverse in language and location. The early part of the story was meant to be a follow-up  ', ' ', 100);
insert into ReviewEntity  (id, name,  description, source, book_id) values (200, ' ','The trilogy is worth reading once. For readers who like fast paced action , this is not a series I would recommend. It progresses quite slowly and the descriptions are lengthy - not really my type. But if you have the patience, the plot is worth it.', ' ', 100);
insert into ReviewEntity  (id, name,  description, source, book_id) values (300,' ','This isnt really like other books, even its imitators, though the best of them are similarly long, variable in pace and diverse in language and location. The early part of the story was meant to be a follow-up to The Hobbit, u ',' ', 200);
insert into ReviewEntity  (id, name,  description, source, book_id) values (400,' ', 'The trilogy is worth reading once. For readers who like fast paced action , this is not a series I would recommend. It progresses quite slowly and the descriptions are lengthy - not really my type. But if you have the patience, the plot is worth it.',' ', 300);
