

CREATE TABLE `users` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(64) NOT NULL
);

INSERT INTO `users` (`id`, `name`, `email`, `password`) VALUES
(1, 'aaaa', 'aaaa', '61be55a8e2f6b4e172338bddf184d6dbee29c98853e0a0485ecee7f27b9af0b4'),
(2, 'qqq', 'qqq', 'a95bc16631ae2b6fadb455ee018da0adc2703e56d89e3eed074ce56d2f7b1b6a'),
(3, 'qw', 'qw', 'd876d59095f13054c120f77202c5378aa25d7787d4adf70980dbb3f2a7125ac1'),
(4, 'gg', 'gg', 'cbd3cfb9b9f51bbbfbf08759e243f5b3519cbf6ecc219ee95fe7c667e32c0a8d');


