drop table if exists pokemon;
drop table if exists forms;
create table pokemon(dex_number int(3) PRIMARY KEY, pokemon_name varchar(14), generation int(3), height int(3), weight int(4), forms int(1));

.read gen1.sql
.read gen2.sql
.read gen3.sql
.read gen4.sql
.read gen5.sql
.read gen6.sql
.read gen7.sql
.read pk-height.sql
.read pk-weig.sql
.read forms-table.sql
