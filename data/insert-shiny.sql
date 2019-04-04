drop table if exists shiny_pokemon;
create table shiny_pokemon(dex_number int(3) PRIMARY KEY, pokemon_name varchar(20), generation int(1), FOREIGN KEY(dex_number) REFERENCES pokemon(dex_number), FOREIGN KEY (generation) REFERENCES pokemon(generation);

.read gen1_shiny.sql
.read gen2_shiny.sql
.read gen3_shiny.sql
.read gen4_shiny.sql
.read gen5_shiny.sql
.read gen6_shiny.sql
.read gen7_shiny.sql
