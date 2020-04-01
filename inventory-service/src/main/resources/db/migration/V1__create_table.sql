create table inventory(
    id varchar(26) primary key,
    product_id varchar(26) not null,
    description text,
    amount bigint not null
);
