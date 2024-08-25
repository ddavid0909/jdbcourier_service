USE [TransportPaketa]
GO
/****** Object:  Trigger [dbo].[TR_TransportOffer_1]    Script Date: 7/2/2024 6:07:37 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE OR ALTER TRIGGER [dbo].[TR_TransportOffer_1] 
   ON  [dbo].[Paket]
   AFTER UPDATE
AS 
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	
	DELETE FROM Ponuda 
	WHERE IdPak IN (SELECT i.IdPak FROM inserted i JOIN deleted d ON i.IdPak = d.IdPak AND i.Status = 1 AND d.Status = 0);
	
	UPDATE Korisnik 
	SET BrPaketa = BrPaketa + 1
	WHERE IdKor IN (SELECT i.IdKor FROM inserted i JOIN deleted d ON i.IdPak = d.IdPak AND i.Status = 3 AND d.Status = 2);
	
	UPDATE Kurir 
	SET BrPaketa = BrPaketa + 1
	WHERE IdKor IN (SELECT i.IdKurir FROM inserted i JOIN deleted d ON i.IdPak = d.IdPak AND i.Status = 3 AND d.Status = 2);
	

	
	

    -- Insert statements for trigger here

END
