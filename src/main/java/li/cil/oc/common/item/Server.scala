package li.cil.oc.common.item

import java.util
import li.cil.oc.common.GuiType
import li.cil.oc.common.inventory.ServerInventory
import li.cil.oc.util.Tooltip
import li.cil.oc.{Settings, OpenComputers}
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import scala.collection.mutable

class Server(val parent: Delegator) extends Delegate {
  val unlocalizedName = "Server"

  override def maxStackSize = 1

  private object HelperInventory extends ServerInventory {
    var container: ItemStack = null
  }

  override def tooltipLines(stack: ItemStack, player: EntityPlayer, tooltip: util.List[String], advanced: Boolean) {
    tooltip.addAll(Tooltip.get(unlocalizedName))
    HelperInventory.container = stack
    HelperInventory.reinitialize()
    val items = mutable.Map.empty[String, Int]
    for (item <- (0 until HelperInventory.getSizeInventory).map(HelperInventory.getStackInSlot) if item != null) {
      val itemName = item.getDisplayName
      items += itemName -> (if (items.contains(itemName)) items(itemName) + 1 else 1)
    }
    if (items.size > 0) {
      tooltip.addAll(Tooltip.get("Server.Components"))
      for (itemName <- items.keys.toArray.sorted) {
        tooltip.add("- " + items(itemName) + "x " + itemName)
      }
    }
    super.tooltipLines(stack, player, tooltip, advanced)
  }

  override def registerIcons(iconRegister: IconRegister) {
    super.registerIcons(iconRegister)

    icon = iconRegister.registerIcon(Settings.resourceDomain + ":server")
  }

  override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer) = {
    if (!player.isSneaking) {
      if (!world.isRemote) {
        player.openGui(OpenComputers, GuiType.Server.id, world, 0, 0, 0)
      }
      player.swingItem()
    }
    stack
  }

}