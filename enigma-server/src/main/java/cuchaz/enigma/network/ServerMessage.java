package cuchaz.enigma.network;

import cuchaz.enigma.network.packet.PacketHelper;
import cuchaz.enigma.translation.representation.entry.Entry;
import cuchaz.enigma.utils.I18n;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public abstract class ServerMessage {
	public final String user;

	public static Chat chat(String user, String message) {
		return new Chat(user, message);
	}

	public static Connect connect(String user) {
		return new Connect(user);
	}

	public static Disconnect disconnect(String user) {
		return new Disconnect(user);
	}

	public static EditDocs editDocs(String user, Entry<?> entry) {
		return new EditDocs(user, entry);
	}

	public static MarkDeobf markDeobf(String user, Entry<?> entry) {
		return new MarkDeobf(user, entry);
	}

	public static RemoveMapping removeMapping(String user, Entry<?> entry) {
		return new RemoveMapping(user, entry);
	}

	public static Rename rename(String user, Entry<?> entry, String newName) {
		return new Rename(user, entry, newName);
	}

	public abstract String translate();

	public abstract Type getType();

	public static ServerMessage read(DataInput input) throws IOException {
		byte typeId = input.readByte();
		if (typeId < 0 || typeId >= Type.values().length) {
			throw new IOException(String.format("Invalid message type ID %d", typeId));
		}

		Type type = Type.values()[typeId];
		String user = PacketHelper.readString(input);
		switch (type) {
			case CHAT:
				String message = PacketHelper.readString(input);
				return chat(user, message);
			case CONNECT:
				return connect(user);
			case DISCONNECT:
				return disconnect(user);
			case EDIT_DOCS:
				Entry<?> entry = PacketHelper.readEntry(input);
				return editDocs(user, entry);
			case MARK_DEOBF:
				entry = PacketHelper.readEntry(input);
				return markDeobf(user, entry);
			case REMOVE_MAPPING:
				entry = PacketHelper.readEntry(input);
				return removeMapping(user, entry);
			case RENAME:
				entry = PacketHelper.readEntry(input);
				String newName = PacketHelper.readString(input);
				return rename(user, entry, newName);
			default:
				throw new IllegalStateException("unreachable");
		}
	}

	public void write(DataOutput output) throws IOException {
		output.writeByte(this.getType().ordinal());
		PacketHelper.writeString(output, this.user);
	}

	private ServerMessage(String user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		ServerMessage message = (ServerMessage) o;
		return Objects.equals(this.user, message.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.user);
	}

	public enum Type {
		CHAT,
		CONNECT,
		DISCONNECT,
		EDIT_DOCS,
		MARK_DEOBF,
		REMOVE_MAPPING,
		RENAME,
	}

	public static final class Chat extends ServerMessage {
		public final String message;

		private Chat(String user, String message) {
			super(user);
			this.message = message;
		}

		@Override
		public void write(DataOutput output) throws IOException {
			super.write(output);
			PacketHelper.writeString(output, this.message);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.chat.text"), this.user, this.message);
		}

		@Override
		public Type getType() {
			return Type.CHAT;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			Chat chat = (Chat) o;
			return Objects.equals(this.message, chat.message);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), this.message);
		}

		@Override
		public String toString() {
			return String.format("Message.Chat { user: '%s', message: '%s' }", this.user, this.message);
		}
	}

	public static final class Connect extends ServerMessage {
		private Connect(String user) {
			super(user);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.connect.text"), this.user);
		}

		@Override
		public Type getType() {
			return Type.CONNECT;
		}

		@Override
		public String toString() {
			return String.format("Message.Connect { user: '%s' }", this.user);
		}
	}

	public static final class Disconnect extends ServerMessage {
		private Disconnect(String user) {
			super(user);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.disconnect.text"), this.user);
		}

		@Override
		public Type getType() {
			return Type.DISCONNECT;
		}

		@Override
		public String toString() {
			return String.format("Message.Disconnect { user: '%s' }", this.user);
		}
	}

	public static final class EditDocs extends ServerMessage {
		public final Entry<?> entry;

		private EditDocs(String user, Entry<?> entry) {
			super(user);
			this.entry = entry;
		}

		@Override
		public void write(DataOutput output) throws IOException {
			super.write(output);
			PacketHelper.writeEntry(output, this.entry);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.edit_docs.text"), this.user, this.entry);
		}

		@Override
		public Type getType() {
			return Type.EDIT_DOCS;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			EditDocs editDocs = (EditDocs) o;
			return Objects.equals(this.entry, editDocs.entry);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), this.entry);
		}

		@Override
		public String toString() {
			return String.format("Message.EditDocs { user: '%s', entry: %s }", this.user, this.entry);
		}
	}

	public static final class MarkDeobf extends ServerMessage {
		public final Entry<?> entry;

		private MarkDeobf(String user, Entry<?> entry) {
			super(user);
			this.entry = entry;
		}

		@Override
		public void write(DataOutput output) throws IOException {
			super.write(output);
			PacketHelper.writeEntry(output, this.entry);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.mark_deobf.text"), this.user, this.entry);
		}

		@Override
		public Type getType() {
			return Type.MARK_DEOBF;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			MarkDeobf markDeobf = (MarkDeobf) o;
			return Objects.equals(this.entry, markDeobf.entry);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), this.entry);
		}

		@Override
		public String toString() {
			return String.format("Message.MarkDeobf { user: '%s', entry: %s }", this.user, this.entry);
		}
	}

	public static final class RemoveMapping extends ServerMessage {
		public final Entry<?> entry;

		private RemoveMapping(String user, Entry<?> entry) {
			super(user);
			this.entry = entry;
		}

		@Override
		public void write(DataOutput output) throws IOException {
			super.write(output);
			PacketHelper.writeEntry(output, this.entry);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.remove_mapping.text"), this.user, this.entry);
		}

		@Override
		public Type getType() {
			return Type.REMOVE_MAPPING;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			RemoveMapping that = (RemoveMapping) o;
			return Objects.equals(this.entry, that.entry);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), this.entry);
		}

		@Override
		public String toString() {
			return String.format("Message.RemoveMapping { user: '%s', entry: %s }", this.user, this.entry);
		}
	}

	public static final class Rename extends ServerMessage {
		public final Entry<?> entry;
		public final String newName;

		private Rename(String user, Entry<?> entry, String newName) {
			super(user);
			this.entry = entry;
			this.newName = newName;
		}

		@Override
		public void write(DataOutput output) throws IOException {
			super.write(output);
			PacketHelper.writeEntry(output, this.entry);
			PacketHelper.writeString(output, this.newName);
		}

		@Override
		public String translate() {
			return String.format(I18n.translate("message.rename.text"), this.user, this.entry, this.newName);
		}

		@Override
		public Type getType() {
			return Type.RENAME;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			if (!super.equals(o)) return false;
			Rename rename = (Rename) o;
			return Objects.equals(this.entry, rename.entry)
					&& Objects.equals(this.newName, rename.newName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), this.entry, this.newName);
		}

		@Override
		public String toString() {
			return String.format("Message.Rename { user: '%s', entry: %s, newName: '%s' }", this.user, this.entry, this.newName);
		}
	}
}
