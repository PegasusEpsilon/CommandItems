package pegasusepsilon.commanditems;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

import net.minecraft.util.EntitySelectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.List;

class RayTraceHelper {
	// server-side raytracing, whee...

	static BlockPos rayTraceBlock (EntityPlayer player) {
		double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		Vec3d start = player.getPositionEyes(1.0F);
		Vec3d look = player.getLook(1.0F);
		Vec3d end = start.addVector(look.x * reach, look.y * reach, look.z * reach);
		try { return player.world.rayTraceBlocks(start, end, false, false, false).getBlockPos(); }
		catch (NullPointerException e) { return null; }
	}

	static Entity rayTraceEntity (EntityPlayer player) {
		double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();

		Vec3d start = player.getPositionEyes(1.0F);
		Vec3d look = player.getLook(1.0F);
		Vec3d end = start.addVector(look.x * reach, look.y * reach, look.z * reach);

		Entity target = null;
		List<Entity> entities = player.world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(start, end), EntitySelectors.NOT_SPECTATING);

		double d0 = 0.0D;

		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);

			double magic = 0.30000001192092896D; // wtf magic number
			AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(magic, magic, magic);
			RayTraceResult result = aabb.calculateIntercept(start, end);

			if (result != null) {
				double d1 = start.squareDistanceTo(result.hitVec);
				if (d1 < d0 || d0 == 0.0D) {
					target = entity;
					d0 = d1;
				}
			}
		}
		return target;
	}
}
