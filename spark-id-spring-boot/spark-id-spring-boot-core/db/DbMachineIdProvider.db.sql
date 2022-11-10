drop table if exists db_machine_id_provider;

create table db_machine_id_provider (
    id bigint not null,
    ip varchar(64) default null,
    primary key (id),
    unique key uk_ip (ip)
);

drop procedure if exists initDbMachineIdProvider;

delimiter //
create procedure initDbMachineIdProvider()
begin
    declare v int;
    set v = 0;
    set autocommit = false;

    LOOP_LABLE:
    loop
        insert into db_machine_id_provider(id) values (v);
        set v = v + 1;
        if v >= 1024 then leave LOOP_LABLE; end if;
    end loop;

    commit;
    set autocommit = true;
end;
//
delimiter ;

call initDbMachineIdProvider;

