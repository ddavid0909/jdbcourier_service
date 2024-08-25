DROP DATABASE IF EXISTS TransportPaketa
GO
CREATE DATABASE TransportPaketa
GO

USE TransportPaketa

CREATE TABLE [Grad]
( 
	[IdGra]              integer identity(1,1)  NOT NULL ,
	[Naziv]              varchar(100)  NOT NULL ,
	[PB]                 integer  NOT NULL 
)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([IdGra] ASC)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XAK1Grad] UNIQUE ([PB]  ASC)
go

CREATE TABLE [Korisnik]
( 
	[IdKor]              integer identity(1,1) NOT NULL ,
	[Admin]				 integer NOT NULL DEFAULT 0 CHECK([Admin] = 0 OR [Admin] = 1),
	[Ime]                varchar(100)  NULL ,
	[Prezime]            varchar(100)  NULL ,
	[KorinickoIme]       varchar(100)  NULL ,
	[Sifra]              varchar(100)  NULL ,
	[BrPaketa]           integer  NULL 
	CONSTRAINT [pocetnaVrednost_1036817347]
		 DEFAULT  0
)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([IdKor] ASC)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XAK1Korisnik] UNIQUE ([KorinickoIme]  ASC)
go

CREATE TABLE [Kurir]
( 
	[BrPaketa]           integer  NOT NULL 
	CONSTRAINT [pocetnaVrednost_1427504949]
		 DEFAULT  0,
	[Profit]             decimal(10,3)  NOT NULL ,
	[Status]             integer  NOT NULL ,
	[IdKor]              integer  NOT NULL ,
	[IdVoz]              integer  NOT NULL 
)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([IdKor] ASC)
go

CREATE TABLE [Opstina]
( 
	[IdOps]              integer identity(1,1) NOT NULL ,
	[Naziv]              varchar(100)  NOT NULL ,
	[x]                  decimal(10,3)  NOT NULL ,
	[y]                  decimal(10,3)  NOT NULL ,
	[IdGra]              integer  NOT NULL 
)
go

ALTER TABLE [Opstina]
	ADD CONSTRAINT [XPKOpstina] PRIMARY KEY  CLUSTERED ([IdOps] ASC)
go

CREATE TABLE [Paket]
( 
	[IdPak]              integer identity(1,1)  NOT NULL ,
	[Tip]                integer  NULL 
	CONSTRAINT [TipGorivaCheck_385436992]
		CHECK  ( [Tip]=0 OR [Tip]=1 OR [Tip]=2 ),
	[Tezina]             decimal(10,3)  NULL ,
	[Status]             integer  NULL 
	CONSTRAINT [PaketStatusCheck_1757995208]
		CHECK  ( [Status]=0 OR [Status]=1 OR [Status]=2 OR [Status]=3 ),
	[Cena]               decimal(10,3)  NULL ,
	[VremePrihvatanja]   datetime  NULL ,
	[IdKor]              integer  NOT NULL,
	[IdOps1]             integer  NOT NULL ,
	[IdOps2]             integer  NOT NULL,
	[IdKurir]	     integer NULL,
	[IdVoznje]		     integer NULL
)
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [XPKPaket] PRIMARY KEY  CLUSTERED ([IdPak] ASC)
go

CREATE TABLE [Voznja]
(
	[IdVoznje] integer identity(1,1) primary key,
	[IdKurir] integer not null,
	[Zavrsena] integer not null default 0,
	[ProfitVoznje] decimal(10,3) null
)


CREATE TABLE [Ponuda]
( 
	[IdPon]              integer identity(1,1) NOT NULL ,
	[Cena]               decimal(10,3)  NOT NULL ,
	[IdPak]              integer  NOT NULL ,
	[IdKor]              integer  NOT NULL 
)
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [XPKPonuda] PRIMARY KEY  CLUSTERED ([IdPon] ASC)
go

CREATE TABLE [Vozilo]
( 
	[IdVoz]              integer identity(1,1) NOT NULL ,
	[RB]                 varchar(20)  NOT NULL ,
	[TipGoriva]          integer  NOT NULL 
	CONSTRAINT [TipGorivaCheck_557658586]
		CHECK  ( [TipGoriva]=0 OR [TipGoriva]=1 OR [TipGoriva]=2 ),
	[Potrosnja]          decimal(10,3)  NOT NULL 
)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([IdVoz] ASC)
go

CREATE TABLE [ZahtevZaKurira]
( 
	[IdKor]              integer  NOT NULL ,
	[IdVoz]              integer  NOT NULL 
)
go

ALTER TABLE [ZahtevZaKurira]
	ADD CONSTRAINT [XPKZahtevZaKurira] PRIMARY KEY  CLUSTERED ([IdKor] ASC,[IdVoz] ASC)
go



ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdKor]) REFERENCES [Korisnik]([IdKor])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdVoz]) REFERENCES [Vozilo]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Opstina]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IdGra]) REFERENCES [Grad]([IdGra])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdKor]) REFERENCES [Korisnik]([IdKor])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdPak]) REFERENCES [Paket]([IdPak])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdKor]) REFERENCES [Kurir]([IdKor])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [ZahtevZaKurira]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdKor]) REFERENCES [Korisnik]([IdKor])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [ZahtevZaKurira]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdVoz]) REFERENCES [Vozilo]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdOps1]) REFERENCES [Opstina]([IdOps])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdOps2]) REFERENCES [Opstina]([IdOps])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdKurir]) REFERENCES [Kurir]([IdKor])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IdVoznje]) REFERENCES [Voznja]([IdVoznje])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Voznja]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([IdKurir]) REFERENCES [Kurir]([IdKor])
		ON DELETE CASCADE
		ON UPDATE NO ACTION
go