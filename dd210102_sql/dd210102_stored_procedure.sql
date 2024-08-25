USE [TransportPaketa]
GO
/****** Object:  StoredProcedure [dbo].[dd210102_stored_procedure]    Script Date: 7/2/2024 6:11:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE OR ALTER PROCEDURE [dbo].[dd210102_stored_procedure]
	-- Add the parameters for the stored procedure here
	@username varchar(100) 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	declare @userId int;
	declare @vehicleId int;

	set @userId = (SELECT IdKor FROM Korisnik WHERE KorinickoIme = @username)
	set @vehicleId = (SELECT IdVoz FROM ZahtevZaKurira WHERE IdKor = @userId)

	if (@vehicleId is null or @userId is null) return 1;
	if (@vehicleId in (SELECT IdVoz FROM Kurir)) return 1;

	begin transaction
	
	delete from ZahtevZaKurira WHERE IdKor = @userId;
	insert into Kurir(IdKor, IdVoz, BrPaketa, Status, Profit) values (@userId, @vehicleId, 0, 0, 0.0)

	commit transaction

	return 0;

END
