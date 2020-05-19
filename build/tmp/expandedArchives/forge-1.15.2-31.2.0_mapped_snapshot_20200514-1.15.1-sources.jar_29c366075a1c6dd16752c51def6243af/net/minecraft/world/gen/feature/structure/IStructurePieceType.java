package net.minecraft.world.gen.feature.structure;

import java.util.Locale;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface IStructurePieceType {
   IStructurePieceType MSCORRIDOR = register(MineshaftPieces.Corridor::new, "MSCorridor");
   IStructurePieceType MSCROSSING = register(MineshaftPieces.Cross::new, "MSCrossing");
   IStructurePieceType MSROOM = register(MineshaftPieces.Room::new, "MSRoom");
   IStructurePieceType MSSTAIRS = register(MineshaftPieces.Stairs::new, "MSStairs");
   IStructurePieceType PCP = register(PillagerOutpostPieces.PillageOutpost::new, "PCP");
   IStructurePieceType NVI = register(VillagePieces.Village::new, "NVi");
   IStructurePieceType NEBCR = register(FortressPieces.Crossing3::new, "NeBCr");
   IStructurePieceType NEBEF = register(FortressPieces.End::new, "NeBEF");
   IStructurePieceType NEBS = register(FortressPieces.Straight::new, "NeBS");
   IStructurePieceType NECCS = register(FortressPieces.Corridor3::new, "NeCCS");
   IStructurePieceType NECTB = register(FortressPieces.Corridor4::new, "NeCTB");
   IStructurePieceType NECE = register(FortressPieces.Entrance::new, "NeCE");
   IStructurePieceType NESCSC = register(FortressPieces.Crossing2::new, "NeSCSC");
   IStructurePieceType NESCLT = register(FortressPieces.Corridor::new, "NeSCLT");
   IStructurePieceType NESC = register(FortressPieces.Corridor5::new, "NeSC");
   IStructurePieceType NESCRT = register(FortressPieces.Corridor2::new, "NeSCRT");
   IStructurePieceType NECSR = register(FortressPieces.NetherStalkRoom::new, "NeCSR");
   IStructurePieceType NEMT = register(FortressPieces.Throne::new, "NeMT");
   IStructurePieceType NERC = register(FortressPieces.Crossing::new, "NeRC");
   IStructurePieceType NESR = register(FortressPieces.Stairs::new, "NeSR");
   IStructurePieceType NESTART = register(FortressPieces.Start::new, "NeStart");
   IStructurePieceType SHCC = register(StrongholdPieces.ChestCorridor::new, "SHCC");
   IStructurePieceType SHFC = register(StrongholdPieces.Corridor::new, "SHFC");
   IStructurePieceType SH5C = register(StrongholdPieces.Crossing::new, "SH5C");
   IStructurePieceType SHLT = register(StrongholdPieces.LeftTurn::new, "SHLT");
   IStructurePieceType SHLI = register(StrongholdPieces.Library::new, "SHLi");
   IStructurePieceType SHPR = register(StrongholdPieces.PortalRoom::new, "SHPR");
   IStructurePieceType SHPH = register(StrongholdPieces.Prison::new, "SHPH");
   IStructurePieceType SHRT = register(StrongholdPieces.RightTurn::new, "SHRT");
   IStructurePieceType SHRC = register(StrongholdPieces.RoomCrossing::new, "SHRC");
   IStructurePieceType SHSD = register(StrongholdPieces.Stairs::new, "SHSD");
   IStructurePieceType SHSTART = register(StrongholdPieces.Stairs2::new, "SHStart");
   IStructurePieceType SHS = register(StrongholdPieces.Straight::new, "SHS");
   IStructurePieceType SHSSD = register(StrongholdPieces.StairsStraight::new, "SHSSD");
   IStructurePieceType TEJP = register(JunglePyramidPiece::new, "TeJP");
   IStructurePieceType ORP = register(OceanRuinPieces.Piece::new, "ORP");
   IStructurePieceType IGLU = register(IglooPieces.Piece::new, "Iglu");
   IStructurePieceType TESH = register(SwampHutPiece::new, "TeSH");
   IStructurePieceType TEDP = register(DesertPyramidPiece::new, "TeDP");
   IStructurePieceType OMB = register(OceanMonumentPieces.MonumentBuilding::new, "OMB");
   IStructurePieceType OMCR = register(OceanMonumentPieces.MonumentCoreRoom::new, "OMCR");
   IStructurePieceType OMDXR = register(OceanMonumentPieces.DoubleXRoom::new, "OMDXR");
   IStructurePieceType OMDXYR = register(OceanMonumentPieces.DoubleXYRoom::new, "OMDXYR");
   IStructurePieceType OMDYR = register(OceanMonumentPieces.DoubleYRoom::new, "OMDYR");
   IStructurePieceType OMDYZR = register(OceanMonumentPieces.DoubleYZRoom::new, "OMDYZR");
   IStructurePieceType OMDZR = register(OceanMonumentPieces.DoubleZRoom::new, "OMDZR");
   IStructurePieceType OMENTRY = register(OceanMonumentPieces.EntryRoom::new, "OMEntry");
   IStructurePieceType OMPENTHOUSE = register(OceanMonumentPieces.Penthouse::new, "OMPenthouse");
   IStructurePieceType OMSIMPLE = register(OceanMonumentPieces.SimpleRoom::new, "OMSimple");
   IStructurePieceType OMSIMPLET = register(OceanMonumentPieces.SimpleTopRoom::new, "OMSimpleT");
   IStructurePieceType OMWR = register(OceanMonumentPieces.WingRoom::new, "OMWR");
   IStructurePieceType ECP = register(EndCityPieces.CityTemplate::new, "ECP");
   IStructurePieceType WMP = register(WoodlandMansionPieces.MansionTemplate::new, "WMP");
   IStructurePieceType BTP = register(BuriedTreasure.Piece::new, "BTP");
   IStructurePieceType SHIPWRECK = register(ShipwreckPieces.Piece::new, "Shipwreck");

   StructurePiece load(TemplateManager p_load_1_, CompoundNBT p_load_2_);

   static IStructurePieceType register(IStructurePieceType type, String key) {
      return Registry.register(Registry.STRUCTURE_PIECE, key.toLowerCase(Locale.ROOT), type);
   }
}