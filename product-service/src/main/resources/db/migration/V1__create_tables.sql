create table category(
    id varchar(26) primary key not null,
    name varchar not null,
    description text
);

create table product(
    id varchar(26) primary key not null,
    name varchar not null,
    description text,
    price numeric(12, 2),
    category_id varchar(26) references category(id) not null
);
